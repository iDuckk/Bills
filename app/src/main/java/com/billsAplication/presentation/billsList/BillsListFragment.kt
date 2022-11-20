package com.billsAplication.presentation.billsList

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.bills.BillsAdapter
import com.billsAplication.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding: FragmentBillsListBinding get() = _binding!!

    private val bundle = Bundle()

    @Inject
    lateinit var viewModel: BillsListViewModel

    @Inject
    lateinit var billAdapter: BillsAdapter

    @Inject
    lateinit var stateColorButton: StateColorButton

    @Inject
    lateinit var sortingDesc: SortingDesc

    @Inject
    lateinit var sortingAsc: SortingAsc

    @Inject
    lateinit var slideView: SlideView

    lateinit var spinnerAdapter: ArrayAdapter<String>
    private var deleteItem = false
    private var visibilityFilterCard = false
    private var listDeleteItems: ArrayList<BillsItem> = ArrayList()
    private var scope = CoroutineScope(Dispatchers.Main)


    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_FULL_LIST_SORT = 101
    private val NONE = "None"

    private val NEXT_MONTH = true
    private val PREV_MONTH = false
    private val CREATE_TYPE = 100
    private val UPDATE_TYPE = 101

    private val component by lazy {
        (requireActivity().application as BillsApplication).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNewList(viewModel.currentDate)

        binding.tvMonth.text = viewModel.currentDate

        binding.cardViewFilter.visibility = View.VISIBLE

        initRecView()

        onBackPressed()

        titleBar()

        filterBar()

        addButton()

        searchButton()

    }

    private fun searchButton() {
        binding.imBillsSearch.setOnClickListener {
            findNavController().navigate(
                R.id.action_billsListFragment_to_searchFragment
            )
        }
    }

    private fun addButton() {
        binding.buttonAddBill.mainLayout.setOnClickListener {
            if (deleteItem) {
                dialogDeleteItems()
            } else {
                bundle.putInt(ADD_BILL_KEY, CREATE_TYPE)
                findNavController().navigate(
                    R.id.action_billsListFragment_to_addBillFragment,
                    bundle
                )
            }
        }
    }

    private fun deleteItems() {
        billAdapter.submitList(null).apply {
            CoroutineScope(Main).launch {
                if (listDeleteItems.isNotEmpty()) {
                    listDeleteItems.forEach {
                        viewModel.delete(it)
                    }
                }
                billAdapter.deleteItemsAfterRemovedItemFromDB()
                deleteItem = false
                listDeleteItems.clear()
            }
        }
    }

    private fun dialogDeleteItems() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog = builder
            .setTitle(getString(R.string.dialog_title_delete_Bills))
            .setMessage(getString(R.string.dialog_message_delete_bills))
            .setPositiveButton(getString(R.string.button_yes)) { dialog, id ->
                deleteItems() //Delete items
            }
            .setNegativeButton(getString(R.string.search_cancel), null)
            .create()
        dialog.show()
    }

    private fun filterBar() {
        visibilityFilterCard = false
        //income list
        binding.checkBoxIncome.setOnClickListener {
            binding.spinnerFilter.setSelection(0)
            filterList(NONE)
        }
        //Expense list
        binding.checkBoxExpense.setOnClickListener {
            binding.spinnerFilter.setSelection(0)
            filterList(NONE)
        }
        //Descending sort
        binding.checkBoxDecDate.setOnClickListener {
            filterList(
                binding.spinnerFilter.getItemAtPosition(binding.spinnerFilter.selectedItemPosition)
                    .toString()
            )
        }
        //Set Spinner
        spinnerCategory()

    }

    //Get list Type (Income or Expense)
    private fun getSortList(type: Int): ArrayList<BillsItem> {
        val list = ArrayList<BillsItem>()
        viewModel.list.value?.forEach {
            if (it.type == type)
                list.add(it)
        }
        return list
    }

    //Sort list after submit if ArrayList
    private fun setDescentSorting(list: ArrayList<BillsItem>) {
        //Cause without remove List, it scrolls down to hte end
        billAdapter.submitList(null)
        if (binding.checkBoxDecDate.isChecked)
            billAdapter.submitList(sortingAsc(list.toMutableList()))
        else
            billAdapter.submitList(sortingDesc(list.toMutableList()))
    }

    //Sort list after submit if ViewModel.list.value!!
    private fun setDescentSorting(list: List<BillsItem>) {
        //Cause without remove List, it scrolls down to hte end
        billAdapter.submitList(null)
        if (binding.checkBoxDecDate.isChecked)
            billAdapter.submitList(sortingAsc(list.toMutableList()))
        else
            billAdapter.submitList(sortingDesc(list.toMutableList()))

    }

    private fun spinnerCategory() {

        createSpinnerAdapter()

        createListCategory()

        onItemSelectListSpinner()

    }

    private fun onItemSelectListSpinner() {
        // Set an on item selected listener for spinner object
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {//parent.getItemAtPosition(position).toString()
                //SetText SIZE
//                if (binding.spinnerFilter.getChildAt(0) != null)
//                    (binding.spinnerFilter.getChildAt(0) as TextView).textSize = 14f
                // Display the selected item text on text view
                if (visibilityFilterCard)
                    filterList(parent.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {// Another interface callback}
            }
        }
    }

    private fun createListCategory() {
        scope.launch {
            //remove previous data
            spinnerAdapter.clear()
            spinnerAdapter.add(NONE)
            //set new list
            val list = ArrayList<String>()
            withContext(Dispatchers.IO) {
                viewModel.getTypeList(TYPE_INCOME).forEach {
                    list.add(it.category)
                }
                withContext(Dispatchers.IO) {
                    viewModel.getTypeList(TYPE_EXPENSES).forEach {
                        list.add(it.category)
                    }
                    withContext(Dispatchers.Main) {
                        spinnerAdapter.addAll(list.distinct().sorted())
                    }
                }
            }
        }
    }

    private fun createListCategoryExpenses() {
        val list = ArrayList<String>()
        scope.launch {
            //remove previous data
            spinnerAdapter.clear()
            spinnerAdapter.add(NONE)
            withContext(Dispatchers.IO) {
                viewModel.getTypeList(TYPE_EXPENSES).forEach {
                    list.add(it.category)
                }
                withContext(Dispatchers.Main) {
                    spinnerAdapter.addAll(list.distinct().sorted())
                }
            }
        }
    }

    private fun createListCategoryIncome() {
        val list = ArrayList<String>()
        scope.launch {
            //remove previous data
            spinnerAdapter.clear()
            spinnerAdapter.add(NONE)
            withContext(Dispatchers.IO) {
                viewModel.getTypeList(TYPE_INCOME).forEach {
                    list.add(it.category)
                }
                withContext(Dispatchers.Main) {
                    spinnerAdapter.addAll(list.distinct().sorted())
                }
            }
        }
    }

    private fun createSpinnerAdapter() {
        val listCategory = ArrayList<String>()
        listCategory.add(NONE)//First item

        spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listCategory
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                // set item text size
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                // set spinner item padding
                view.setPadding(
                    5.toDp(context), // left
                    3.toDp(context), // top
                    5.toDp(context), // right
                    3.toDp(context) // bottom
                )
                return view
            }
        }

        binding.spinnerFilter.adapter = spinnerAdapter
    }

    private fun filterList(value: String) {
        if (value != NONE) {
            if (binding.checkBoxIncome.isChecked && !binding.checkBoxExpense.isChecked) {
                setDescentSorting(spinnerItemList(TYPE_INCOME, value))
            } else if (binding.checkBoxExpense.isChecked && !binding.checkBoxIncome.isChecked) {
                setDescentSorting(spinnerItemList(TYPE_EXPENSES, value))
            } else {
                setDescentSorting(spinnerItemList(TYPE_FULL_LIST_SORT, value))
            }
        } else {
            if (binding.checkBoxIncome.isChecked && !binding.checkBoxExpense.isChecked) {
                setDescentSorting(getSortList(TYPE_INCOME))
                createListCategoryIncome()
            } else if (binding.checkBoxExpense.isChecked && !binding.checkBoxIncome.isChecked) {
                setDescentSorting(getSortList(TYPE_EXPENSES))
                createListCategoryExpenses()
            } else {
                if (viewModel.list.value != null)
                    setDescentSorting(viewModel.list.value!!)
                else {
                    setNewList(viewModel.currentDate)
                }
                createListCategory()
            }
        }
    }

    //Get list if Spinner chosen a Category
    private fun spinnerItemList(type: Int, value: String): ArrayList<BillsItem> {
        val listCat = ArrayList<BillsItem>()
        //If chosen "None"
        if (type == TYPE_FULL_LIST_SORT) {
            viewModel.list.value?.forEach {
                if (it.category == value)
                    listCat += it
            }
        } else {
            getSortList(type).forEach {
                if (it.category == value)
                    listCat += it
            }
        }
        return listCat
    }

    private fun titleBar() {
        //Sorting
        binding.imBillsFilter.setOnClickListener {
            if (visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter),100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
                //Cause without remove List, it scrolls down to the end
                billAdapter.submitList(null)
                if (viewModel.list.value != null)
                    billAdapter.submitList(sortingDesc(viewModel.list.value!!.toMutableList()))
            } else {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 0, 100)
                visibilityFilterCard = true
            }
        }
        //Set month`s text in bar
        binding.tvMonth.setOnClickListener {
            viewModel.currentDate = viewModel.currentDate()
            if (viewModel.currentDate != binding.tvMonth.text.toString()) {
                binding.tvMonth.text = viewModel.currentDate
                viewModel.defaultMonth()
                //Set off filter card
                if(visibilityFilterCard) {
                    slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                    visibilityFilterCard = false
                    setDefaultSortingViews()
                }
                //set a new list
                setNewList(viewModel.currentDate)
            }
        }
        //Previous month
        binding.imBackMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //Set off filter card
            if(visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
            }
            //set a new list
            setNewList(viewModel.currentDate)
        }
        //Next month
        binding.imNextMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //Set off filter card
            if(visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
            }
            //set a new list
            setNewList(viewModel.currentDate)
        }

        binding.imBookmarks.setOnClickListener {
            findNavController().navigate(
                R.id.action_billsListFragment_to_bookmarksFragment,
            )
        }
    }

    @SuppressLint("ResourceType", "CutPasteId", "UseCompatLoadingForDrawables")
    private fun setBackColorAddButton() {
        //check text for null
        val check = binding.tvTotalNum.text.toString().replace(",", "")


        if (check.toDouble() > 0) {
            //set color of icon nav bottom income
            (context as InterfaceMainActivity).navBottom()
                .itemIconTintList =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav_income)
            //set color of text nav bottom income
            (context as InterfaceMainActivity).navBottom()
                .itemTextColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav_income)
            //set background income
            binding.buttonAddBill.relativeLayout.background =
                requireActivity().getDrawable(R.drawable.double_color_button_income)
//            //set color effect
            (context as InterfaceMainActivity).navBottom()
                .itemRippleColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav_income)
            //Send color for ShopList
            stateColorButton.colorAddButton =
                requireActivity().getDrawable(R.drawable.double_color_button_income)
            stateColorButton.colorButtons = requireActivity().getColor(R.color.text_income)
            stateColorButton.stateNavBot =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav_income)
        } else if (check.toDouble() < 0) {
            //set color of icon nav bottom expenses
            (context as InterfaceMainActivity).navBottom()
                .itemIconTintList =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //set color of text nav bottom expenses
            (context as InterfaceMainActivity).navBottom()
                .itemTextColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //set background expenses
            binding.buttonAddBill.relativeLayout.background =
                requireActivity().getDrawable(R.drawable.double_color_button_expenses)
//            //set color effect
            (context as InterfaceMainActivity).navBottom()
                .itemRippleColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //Send color for ShopList
            stateColorButton.colorAddButton =
                requireActivity().getDrawable(R.drawable.double_color_button_expenses)
            stateColorButton.colorButtons = requireActivity().getColor(R.color.text_expense)
            stateColorButton.stateNavBot =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
        } else {
            //set color of icon nav bottom
            (context as InterfaceMainActivity).navBottom()
                .itemIconTintList =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //set color of text nav bottom
            (context as InterfaceMainActivity).navBottom()
                .itemTextColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //set background
            binding.buttonAddBill.relativeLayout.background =
                requireActivity().getDrawable(R.drawable.double_color_button)
            //set color effect
            (context as InterfaceMainActivity).navBottom()
                .itemRippleColor =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
            //Send color for ShopList
            stateColorButton.colorAddButton =
                requireActivity().getDrawable(R.drawable.double_color_button)
            stateColorButton.colorButtons = requireActivity().getColor(R.color.text_expense)
            stateColorButton.stateNavBot =
                requireActivity().getColorStateList(R.drawable.selector_item_bot_nav)
        }
    }

    private fun initRecView() {
        with(binding.recViewBill) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = billAdapter
            binding.recViewBill.itemAnimator = null
        }

        billAdapter.isTitleIncome.observe(viewLifecycleOwner) {
            binding.tvIncomeNum.text = it
        }

        billAdapter.isTitleExpense.observe(viewLifecycleOwner) {
            binding.tvExpenseNum.text = it
        }

        billAdapter.isTitleTotal.observe(viewLifecycleOwner) {
            binding.tvTotalNum.text = it
            setBackColorAddButton()
            resizeText()
        }

        billAdapter.onClickListenerBillItem = {
            bundle.putInt(ADD_BILL_KEY, UPDATE_TYPE)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
        }

        billAdapter.onLongClickListenerBillItem = {
            listDeleteItems = it
            //Set off filter card
            if(visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
            }
        }
        //Highlight item
        billAdapter.isHighlight.observe(viewLifecycleOwner) {
            deleteItem = it
            if (it) {
                deleteItem = it
                binding.buttonAddBill.imageButton.setImageResource(R.drawable.ic_delete_forever)
                binding.cardViewBar.visibility = View.GONE
                binding.cardViewBudget.visibility = View.GONE
                (context as InterfaceMainActivity).navBottom().visibility = View.GONE
            } else {
                binding.buttonAddBill.imageButton.setImageResource(R.drawable.ic_add)
                binding.cardViewBar.visibility = View.VISIBLE
                binding.cardViewBudget.visibility = View.VISIBLE
                (context as InterfaceMainActivity).navBottom().visibility = View.VISIBLE
                //set a new list
                setNewList(viewModel.currentDate)
            }
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                @SuppressLint("NotifyDataSetChanged")
                override fun handleOnBackPressed() {
                    if (!deleteItem) {
                        requireActivity().finish()
                    } else {
                        deleteItem = false
                        listDeleteItems.clear()
                        billAdapter.deleteItemsAfterRemovedItemFromDB()
                        billAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
    }

    private fun setNewList(month: String) {
        //set a new list
        scope.launch {
            viewModel.getMonth(month)
            viewModel.list.observe(viewLifecycleOwner) {
                //If list null
                if (it.isNullOrEmpty())
                    billAdapter.setAmount()
                //Set list to Adapter
                try {
                    billAdapter.submitList(sortingDesc(it.toMutableList()))
                } catch (e: NumberFormatException) {
                    Log.w("TAG", e.message!!)
                }
            }
        }
    }

    private fun setDefaultSortingViews() {
        binding.checkBoxIncome.isChecked = false
        binding.checkBoxExpense.isChecked = false
        binding.checkBoxDecDate.isChecked = false
        binding.spinnerFilter.setSelection(0)
    }

    @SuppressLint("SetTextI18n")
    private fun resizeText() {
        //Resize text in views if value is huge
        if (binding.tvIncomeNum.text.length > 13
            || binding.tvExpenseNum.text.length > 13
            || binding.tvTotalNum.text.length > 13
        ) {
            binding.tvIncomeNum.textSize = 11F
            binding.tvExpenseNum.textSize = 11F
            binding.tvTotalNum.textSize = 11F
        } else {
            binding.tvIncomeNum.textSize = 18F
            binding.tvExpenseNum.textSize = 18F
            binding.tvTotalNum.textSize = 18F
        }
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

    // Extension method to convert pixels to dp
    fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    override fun onResume() {
        super.onResume()
        if(!scope.isActive)
            scope = CoroutineScope(Dispatchers.Main)
        (context as InterfaceMainActivity).navBottom().visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        //Set off filter card
        if(visibilityFilterCard) {
            slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
            visibilityFilterCard = false
            setDefaultSortingViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
        _binding = null
    }

}