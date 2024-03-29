package com.billsAplication.presentation.billsList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.bills.BillsAdapter
import com.billsAplication.utils.*
import com.billsAplication.utils.Result
import com.yandex.mobile.ads.banner.AdSize
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
    @Inject
    lateinit var crossfade: CrossFade
    @Inject
    lateinit var fadeInView: FadeInView
    @Inject
    lateinit var fadeOutView: FadeOutView
    @Inject
    lateinit var motionViewY: MotionViewY

    lateinit var spinnerAdapter: ArrayAdapter<String>
    private var deleteItem = false
    private var visibilityFilterCard = false
    private var listDeleteItems: ArrayList<BillsItem> = ArrayList()
    private var scope = CoroutineScope(Dispatchers.Main)


    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val TYPE_NOTE_RECEIVE = "type_note_receive"

    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_EQUALS = 2
    private var TYPE_BILL = "type_bill"
    private val TYPE_FULL_LIST_SORT = 101
    private val NONE = "None"
    private val NEXT_MONTH = 20
    private val PREV_MONTH = 21
    private val CURRENT_MONTH = 22
    private val CREATE_TYPE = 100
    private val UPDATE_TYPE = 101
    private val TAG = "BillsListFragment"

    private val heightNavBottom by lazy {
        (context as InterfaceMainActivity).navBottom().height
    }

    private val mainActivity by lazy {
        (context as InterfaceMainActivity)
    }

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

        observerList()

        binding.cardViewFilter.visibility = View.VISIBLE

        initRecView()

        onBackPressed()

        titleBar()

        addButton()

        searchButton()

        filterBar()

    }

    private fun observerList() {
        viewModel.stateList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Result -> {
                    state.list.let { billAdapter.submitList(sortingDesc(state.list)) }
                }
                is Error -> {
                    Log.e("TAG", state.exception)
                }
                is TotalAmountBar -> {
                    binding.tvIncomeNum.text = state.inc
                    binding.tvExpenseNum.text = state.exp
                    binding.tvTotalNum.text = state.tot
                    resizeText()
                }
                is ColorState -> {
                    setBackColorAddButton(state.type)
                }
            }
            //Get off splash
            scope.launch {
                if(requireActivity() is InterfaceMainActivity) {
                    mainActivity.splash()
                }
                //if we receive note string from other app
                intentActionSendText()
            }
        }
    }

    private fun intentActionSendText() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeAction = sharedPref.getBoolean(TYPE_NOTE_RECEIVE, false)
        if (typeAction) {
            if(requireActivity() is InterfaceMainActivity) {
                mainActivity.navBottom().selectedItemId = R.id.shopListFragment
            }
        }
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
        viewModel.listBills.forEach {
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

    private fun spinnerCategory() {
        createSpinnerAdapter()
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

    private fun createListCategory(type: Int) {
        //remove previous data
        spinnerAdapter.clear()
        spinnerAdapter.add(NONE)
        //set new list
        val list = when(type){
            TYPE_INCOME -> viewModel.incomeCategorySpinner()
            TYPE_EXPENSES -> viewModel.expenseCategorySpinner()
            else -> {
                val wholeList = ArrayList<String>()
                wholeList.addAll(viewModel.incomeCategorySpinner())
                wholeList.addAll(viewModel.expenseCategorySpinner())
                wholeList
            }
        }
        spinnerAdapter.addAll(list.distinct().sorted())
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
                createListCategory(TYPE_INCOME)
            } else if (binding.checkBoxExpense.isChecked && !binding.checkBoxIncome.isChecked) {
                setDescentSorting(getSortList(TYPE_EXPENSES))
                createListCategory(TYPE_EXPENSES)
            } else {
                if (viewModel.listBills != null)
                    setDescentSorting(viewModel.listBills)
                else {
                    viewModel.getStateList(viewModel.currentDate())
                }
                createListCategory(TYPE_FULL_LIST_SORT)
            }
        }
    }

    //Get list if Spinner chosen a Category
    private fun spinnerItemList(type: Int, value: String): ArrayList<BillsItem> {
        val listCat = ArrayList<BillsItem>()
        //If chosen "None"
        if (type == TYPE_FULL_LIST_SORT) {
            viewModel.listBills.forEach {
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
        //Close delete mode
        binding.imCloseDel.setOnClickListener {
            deleteItem = false
            listDeleteItems.clear()
            billAdapter.deleteItemsAfterRemovedItemFromDB()
            billAdapter.notifyDataSetChanged()
        }
        //Sorting
        binding.imBillsFilter.setOnClickListener {

            createListCategory(TYPE_FULL_LIST_SORT)

            if (visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
                //Cause without remove List, it scrolls down to the end
                billAdapter.submitList(null)
                if (viewModel.listBills != null)
                    billAdapter.submitList(sortingDesc(viewModel.listBills.toMutableList()))
            } else {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 0, 100)
                visibilityFilterCard = true
            }
        }

        viewModel.month.observe(viewLifecycleOwner) {
            binding.tvMonth.text = it
            //Set off filter card
            if (visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
            }
        }

        //Set month`s text in bar
        binding.tvMonth.setOnClickListener {
            if (viewModel.currentDate() != binding.tvMonth.text.toString()) {
                viewModel.changeMonth(CURRENT_MONTH)
            }
        }
        //Previous month
        binding.imBackMonth.setOnClickListener {
            viewModel.changeMonth(PREV_MONTH)
        }
        //Next month
        binding.imNextMonth.setOnClickListener {
            viewModel.changeMonth(NEXT_MONTH)
        }

        binding.imBookmarks.setOnClickListener {
            findNavController().navigate(
                R.id.action_billsListFragment_to_bookmarksFragment,
            )
        }
    }

    @SuppressLint("ResourceType", "CutPasteId", "UseCompatLoadingForDrawables")
    private fun setBackColorAddButton(type: Int) {
        val t = if (binding.tvMonth.text == viewModel.currentDate()) {
            type
        } else {
            //get saved type
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.getInt(TYPE_BILL, TYPE_EQUALS)
        }
        with(stateColorButton) {
            if(requireActivity() is InterfaceMainActivity) {
                with(mainActivity.navBottom()) {
                    stateNavBot(t).let {
                        if (itemIconTintList != it) { //if do it again
                            itemIconTintList = it //set color of icon nav bottom income
                            itemTextColor = it //set color of text nav bottom income
                            itemRippleColor = it //set color effect
                        }
                    }
                }
            }
            //set background income
            binding.buttonAddBill.relativeLayout.background = colorAddButton(t)
            //Set filter colors
            filterViewsColor(t)
            //Save type colors
            setSharePrefColors(t)
        }
    }

    private fun filterViewsColor(type: Int) {
        val buttonStates = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ), intArrayOf(
                requireActivity().getColor(R.color.default_background), //track unChecked
                stateColorButton.colorButtons(type),
                requireActivity().getColor(R.color.default_background)
            )
        )
        binding.checkBoxIncome.buttonTintList = buttonStates
        binding.checkBoxExpense.buttonTintList = buttonStates
        binding.checkBoxDecDate.buttonTintList = buttonStates
    }

    private fun setSharePrefColors(type: Int) {
        val sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(TYPE_BILL, type)
            apply()
        }
    }

    @SuppressLint("ResourceType")
    private fun initRecView() {
        with(binding.recViewBill) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = billAdapter
            binding.recViewBill.itemAnimator = null
        }

        billAdapter.onClickListenerBillItem = {
            bundle.putInt(ADD_BILL_KEY, UPDATE_TYPE)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
        }

        billAdapter.onLongClickListenerBillItem = {
            listDeleteItems = it
            //Set off filter card
            if (visibilityFilterCard) {
                slideView(requireView().findViewById<CardView>(R.id.cardView_filter), 100, 0)
                visibilityFilterCard = false
                setDefaultSortingViews()
            }
        }
        //Highlight item
        billAdapter.isHighlight.observe(viewLifecycleOwner) {
            deleteItem = it
            if (it) { //GONE VIEWS
                deleteItem = it
                //AddButton
                crossfade(binding.buttonAddBill.imageButtonBas, binding.buttonAddBill.imageButton)
                //Bar
                slideView(binding.constraintMainBar, binding.cardViewBar.height, 0)
                fadeInView(binding.imCloseDel)
                //NavBottom
                if(requireActivity() is InterfaceMainActivity) {
                    mainActivity.navBottom().visibility = View.INVISIBLE
                }
                //BudgetBar
                if (visibilityFilterCard) {
                    slideView(binding.cardViewFilter, 100, 0)
                }
            } else {
                //AddButton
                if (binding.imCloseDel.visibility == View.VISIBLE) {
                    crossfade(
                        binding.buttonAddBill.imageButton,
                        binding.buttonAddBill.imageButtonBas
                    )
                    //Bar
                    slideView(binding.constraintMainBar, 0, binding.cardViewBar.height)
                    fadeOutView(binding.imCloseDel)
                    //NavBottom
                    if(requireActivity() is InterfaceMainActivity) {
                        mainActivity.navBottom().visibility = View.VISIBLE
                    }

                    //set a new list
                    viewModel.getStateList(viewModel.currentDate())
                }
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
                        requireActivity().finishAffinity()
                        requireActivity().finish()
//                        isEnabled = false
//                        requireActivity().onBackPressed()
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
        if (!scope.isActive) {
            scope = CoroutineScope(Dispatchers.Main)
            createListCategory(TYPE_FULL_LIST_SORT)
        }
        if(requireActivity() is InterfaceMainActivity) {
            if (mainActivity.navBottom().visibility == View.GONE ||
                mainActivity.navBottom().visibility == View.INVISIBLE)
                fadeInView(mainActivity.navBottom())
        }
        viewModel.getStateList(binding.tvMonth.text.toString())
    }

    override fun onPause() {
        super.onPause()
        //Set off filter card
        if (visibilityFilterCard) {
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