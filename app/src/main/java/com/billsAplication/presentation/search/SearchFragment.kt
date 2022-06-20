package com.billsAplication.presentation.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentSearchBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.bills.BillsAdapter
import com.billsAplication.presentation.chooseCategory.ChooseCategoryDialog
import com.billsAplication.presentation.chooseCategory.ChooseMonthDialog
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.log


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    @Inject
    lateinit var viewModel: SearchViewModel

    @Inject
    lateinit var billAdapter: BillsAdapter

    private val bundle = Bundle()
    private val UPDATE_TYPE_SEARCH = 103
    private val BILL_ITEM_KEY = "bill_item_key"
    private val ADD_BILL_KEY = "add_bill_key"
    private val REQUESTKEY_CATEGORY_ITEM = "RequestKey_Category_item"
    private val KEY_CATEGORY_LIST_FRAGMENT = "key_category_from_fragment"
    private val KEY_CHOSEN_CATEGORY_LIST_FRAGMENT = "key_CHOSEN_category_from_fragment"
    private val KEY_CATEGORY_ITEMS_DIALOG = "key_category_from_dialog"
    private val TAG_DIALOG_CATEGORY = "Dialog Category"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_CATEGORY = 2
    private val TYPE_NOTE = 3
    private val listNote: ArrayList<String> = ArrayList()
    private val EMPTY_STRING = ""
    private val KEY_MONTH_LIST_FRAGMENT = "key_month_from_fragment"
    private val KEY_MONTH_ITEMS_DIALOG = "key_month_from_dialog"
    private val KEY_CHOSEN_MONTH_LIST_FRAGMENT = "key_CHOSEN_month_from_fragment"
    private val REQUESTKEY_MONTH_ITEM = "RequestKey_MONTH_item"
    private val TAG_DIALOG_MONTH = "Dialog Month"

    private var income = BigDecimal(0)
    private var expense = BigDecimal(0)
    private var titleIncome = MutableLiveData<BigDecimal>()
    private var titleExpense = MutableLiveData<BigDecimal>()
    private var titleTotal = MutableLiveData<BigDecimal>()
    private var imageRoll = false
    private var deleteItem = false
    private var allItemList = ArrayList<BillsItem>()
    private var listDeleteItems = ArrayList<BillsItem>()
    private var categoryList = arrayOf<String>()
    private var monthList = arrayOf<String>()
    private var chosenItemsCategory = booleanArrayOf()
    private var chosenItemsMonth = booleanArrayOf()


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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .visibility = View.GONE

        viewModel.list.observe(viewLifecycleOwner) { list ->

            list.forEach {
                //Create list of Notes
                if (it.note != EMPTY_STRING && it.type != TYPE_NOTE)
                    listNote.add(it.note)
                //Create Category list
                if (it.category != EMPTY_STRING && it.type != TYPE_NOTE)
                    categoryList += it.category
                //Create full list
                if (it.type != TYPE_CATEGORY && it.type != TYPE_NOTE)
                    allItemList.add(it)
                //Create Month list
                if (it.month != EMPTY_STRING)
                    monthList += it.month
            }

            categoryList = categoryList.distinct().toTypedArray()
            initAutoCompleteEditText()

//            setAmountBar(list)
//            setListAdapter(allItemList)
        }

        titleBar()

        titleAmount()

        searchViews()

        initRecView()

        buttonDelete()

    }

    private fun titleAmount() {
        //Set title Total
        titleTotal.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvSearchTotalNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
        //Set title Expense
        titleExpense.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvSearchExpenseNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
        //Set title Income
        titleIncome.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvSearchIncomeNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
    }

    private fun buttonDelete() {
        binding.bSearchDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (listDeleteItems.isNotEmpty()) {
                    listDeleteItems.forEach {
                        viewModel.delete(it)
                        allItemList.remove(it)
                    }
                }
                billAdapter.deleteItemsAfterRemovedItemFromDB()
                deleteItem = false
                listDeleteItems.clear()
            }
            setListAdapter(allItemList)
        }
    }

    private fun searchViews() {
        imageRoll()

        noteView()

        categoryView()

        amountMin()

        amountMax()

        periodVew()

    }

    @SuppressLint("SetTextI18n")
    private fun chipGroup() {
        if (imageRoll) {
            //Chip of note
            if (binding.edSearchNote.text.isNotEmpty()) {
                binding.chipNote.visibility = View.VISIBLE
                binding.chipNote.text = binding.edSearchNote.text
            }
            //chip of category
            if (binding.etSearchCategory.text.isNotEmpty()) {
                binding.chipCategory.visibility = View.VISIBLE
                binding.chipCategory.text = binding.etSearchCategory.text
            }
            //Chip of min
            if (binding.etSearchAmountMin.text!!.isNotEmpty()) {
                binding.chipMin.visibility = View.VISIBLE
                binding.chipMin.text =
                    getString(R.string.amount_min) + binding.etSearchAmountMin.text
            }
            //Chip of max
            if (binding.etSearchAmountMax.text!!.isNotEmpty()) {
                binding.chipMax.visibility = View.VISIBLE
                binding.chipMax.text =
                    getString(R.string.amount_max) + binding.etSearchAmountMax.text
            }
            //chip of period
            if (binding.etSearchPeriod.text.isNotEmpty()) {
                binding.chipPeriod.visibility = View.VISIBLE
                binding.chipPeriod.text = binding.etSearchPeriod.text
            }
        } else {
            binding.chipNote.visibility = View.GONE
            binding.chipCategory.visibility = View.GONE
            binding.chipMin.visibility = View.GONE
            binding.chipMax.visibility = View.GONE
            binding.chipPeriod.visibility = View.GONE
        }
    }

    private fun periodVew() {
        binding.etSearchPeriod.setOnFocusChangeListener { view, b ->
            if (binding.etSearchPeriod.isFocused) {
                monthList = monthList.distinct().toTypedArray() //Delete repeated Items
                setChosenMonthItemsDialog() // Set list chosen items
                val dialog = ChooseMonthDialog()
                val args = Bundle()
                //sent list to Dialog
                args.putStringArray(KEY_MONTH_LIST_FRAGMENT, monthList)
                //sent boolean list of chosen item
                args.putBooleanArray(KEY_CHOSEN_MONTH_LIST_FRAGMENT, chosenItemsMonth)
                dialog.arguments = args
                //Show dialog
                dialog.show(requireActivity().supportFragmentManager, TAG_DIALOG_MONTH)
                //Receive chosen items from Dialog
                dialog.setFragmentResultListener(REQUESTKEY_MONTH_ITEM) { requestKey, bundle ->
                    val list = bundle.getStringArray(KEY_MONTH_ITEMS_DIALOG) //get chosen Items
                    binding.etSearchPeriod.setText(categoriesString(list))
                    //Set List
                    performSearch()
                }
                binding.etSearchPeriod.clearFocus()
            }
        }
    }

    private fun amountMax() {
        binding.etSearchAmountMax.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch()
                v.hideKeyboard()
                v.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        binding.etSearchAmountMax.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Resize text in views if value is huge
                if (p0?.length != 0) //This is because keyboard doesn't open in first Click
                    if (binding.etSearchAmountMax.text!!.length > 13
                    ) {
                        binding.etSearchAmountMax.textSize = 12F
                    } else {
                        binding.etSearchAmountMax.textSize = 18F
                    }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun amountMin() {
        binding.etSearchAmountMin.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch()
                v.hideKeyboard()
                v.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        binding.etSearchAmountMin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                //Resize text in views if value is huge
                if (p0?.length != 0) //This is because keyboard doesn't open in first Click
                    if (binding.etSearchAmountMin.text!!.length > 13
                    ) {
                        binding.etSearchAmountMin.textSize = 12F
                    } else {
                        binding.etSearchAmountMin.textSize = 18F
                    }
            }
        })
    }

    private fun categoryView() {
        binding.etSearchCategory.setOnFocusChangeListener { view, b ->
            if (binding.etSearchCategory.isFocused) {
                setChosenCategoryItemsDialog() // Set list chosen items
                val dialog = ChooseCategoryDialog()
                val args = Bundle()
                //sent list to Dialog
                args.putStringArray(KEY_CATEGORY_LIST_FRAGMENT, categoryList)
                //sent boolean list of chosen item
                args.putBooleanArray(KEY_CHOSEN_CATEGORY_LIST_FRAGMENT, chosenItemsCategory)
                dialog.arguments = args
                //Show dialog
                dialog.show(requireActivity().supportFragmentManager, TAG_DIALOG_CATEGORY)
                //Receive chosen items from Dialog
                dialog.setFragmentResultListener(REQUESTKEY_CATEGORY_ITEM) { requestKey, bundle ->
                    val list = bundle.getStringArray(KEY_CATEGORY_ITEMS_DIALOG) //get chosen Items
                    binding.etSearchCategory.setText(categoriesString(list))
                    //Set List
                    performSearch()
                }
                binding.etSearchCategory.clearFocus()
            }
        }
    }

    private fun setChosenMonthItemsDialog() {
        var chosenCategoryList = arrayOf<String>()
        var booleanList = booleanArrayOf()
        //Set it none of item is chosen
        if (binding.etSearchPeriod.text.isNullOrEmpty())
            monthList.forEach { booleanList += false }
        else {
            // get list of chosen months from Edittext
            chosenCategoryList = binding.etSearchPeriod.text.split(", ").toTypedArray()
            //Set chosen items
            monthList.forEach { item ->
                if (chosenCategoryList.find { it == item } == item)
                    booleanList += true
                else
                    booleanList += false
            }
        }
        chosenItemsMonth = booleanList
    }

    private fun setChosenCategoryItemsDialog() {
        var chosenCategoryList = arrayOf<String>()
        var booleanList = booleanArrayOf()
        //Set it none of item is chosen
        if (binding.etSearchCategory.text.isNullOrEmpty())
            categoryList.forEach { booleanList += false }
        else {
            // get list of chosen categories from Edittext
            chosenCategoryList = binding.etSearchCategory.text.split(", ").toTypedArray()
            //Set chosen items
            categoryList.forEach { item ->
                if (chosenCategoryList.find { it == item } == item)
                    booleanList += true
                else
                    booleanList += false
            }
        }
        chosenItemsCategory = booleanList
    }

    private fun categoriesString(list: Array<String>?): String {
        var categories = ""
        if (list?.size == 1) {    //If one item
            categories += list.first()
        } else {
            list?.forEach {
                categories += it
                if (list.last() != it)   //if it not last item add ', '
                    categories += ", "
            }
        }
        return categories
    }

    private fun noteView() {
        binding.edSearchNote.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch()
                v.hideKeyboard()
                v.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        val list: ArrayList<BillsItem> = allItemList.clone() as ArrayList<BillsItem>
        val listCategory = binding.etSearchCategory.text.split(", ").toTypedArray()
        val listPeriod = binding.etSearchPeriod.text.split(", ").toTypedArray()
        //List Note sorting
        if (binding.edSearchNote.text.isNotEmpty())
            allItemList.forEach {
                if (it.note != binding.edSearchNote.text.toString())
                    list.remove(it)
            }
        // List Category
        if (binding.etSearchCategory.text.isNotEmpty())
            allItemList.forEach { item ->
                if (!listCategory.contains(item.category))
                    list.remove(item)
            }
        // List Min View
        if (binding.etSearchAmountMin.text!!.isNotEmpty())
            allItemList.forEach { item ->
                if (item.amount.replace(",", "")
                    < binding.etSearchAmountMin.text.toString().replace(",", "")
                )
                    list.remove(item)
            }
        // List Max View
        if (binding.etSearchAmountMax.text!!.isNotEmpty())
            allItemList.forEach { item ->
                if (item.amount.replace(",", "")
                    > binding.etSearchAmountMax.text.toString().replace(",", "")
                )
                    list.remove(item)
            }
        //  List Period View
        if (binding.etSearchPeriod.text.isNotEmpty())
            allItemList.forEach { item ->
                if (!listPeriod.contains(item.month))
                    list.remove(item)
            }

        setListAdapter(list)
    }

    private fun setListAdapter(list: ArrayList<BillsItem>) {
        billAdapter.submitList(list.sortedByDescending { item -> sortingListValue(item.date) }
            .toList())
        setAmountBar(list)
    }

    private fun sortingListValue(date: String): Long{
        if(date != EMPTY_STRING) {
            val day = date.dropLast(8)
            val month = date.drop(3).dropLast(5)
            val year = date.drop(6)

            val c = Calendar.getInstance()
            c.set(year.toInt(),month.toInt(),day.toInt())

            return c.timeInMillis
        }
        return 0
    }

    private fun imageRoll() {
        binding.imRollViews.setOnClickListener {
            if (imageRoll) {
                binding.imRollViews.setImageResource(R.drawable.ic_arrow_up)
                imageRoll = false
                binding.cardViewSearch.visibility = View.VISIBLE
            } else {
                binding.imRollViews.setImageResource(R.drawable.ic_arrow_down)
                imageRoll = true
                binding.cardViewSearch.visibility = View.GONE
            }
            chipGroup()
        }
    }

    private fun titleBar() {
        binding.imSearchBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_billsListFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecView() {
        with(binding.recViewBillSearch) {
            layoutManager = LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
            adapter = billAdapter
            binding.recViewBillSearch.itemAnimator = null
        }

        billAdapter.onClickListenerBillItem = {
            bundle.putInt(ADD_BILL_KEY, UPDATE_TYPE_SEARCH)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_searchFragment_to_addBillFragment, bundle)
        }

        billAdapter.onLongClickListenerBillItem = {
            listDeleteItems = it
            setViewsVisibility(false)
        }
        //Highlight item
        billAdapter.isHighlight.observe(viewLifecycleOwner) {
            deleteItem = it
            if (it) {
                deleteItem = it
                setViewsVisibility(false)
            } else {
                setViewsVisibility(true)
            }
        }
    }

    private fun setViewsVisibility(b: Boolean) {
        if (b) {
            binding.bSearchDelete.visibility = View.GONE
            binding.cardViewSearch.visibility = View.VISIBLE
            binding.cardViewSearchBar.visibility = View.VISIBLE
            binding.cardViewRollImage.visibility = View.VISIBLE
            binding.cardViewSearchBudget.visibility = View.VISIBLE
        } else {
            binding.bSearchDelete.visibility = View.VISIBLE
            binding.cardViewSearch.visibility = View.GONE
            binding.cardViewSearchBar.visibility = View.GONE
            binding.cardViewRollImage.visibility = View.GONE
            binding.cardViewSearchBudget.visibility = View.GONE
        }
    }

    private fun setAmountBar(list: List<BillsItem>) {
        list.forEach {
            //Create full list
            if (it.type != TYPE_CATEGORY)
            //Create amount for title amountTextView
                when (it.type) {
                    TYPE_INCOME ->
                        income += BigDecimal(it.amount.replace(",", ""))
                    TYPE_EXPENSES ->
                        expense += BigDecimal(it.amount.replace(",", ""))
                }
        }

        titleIncome.postValue(income)
        titleExpense.postValue(expense)
        titleTotal.postValue(income - expense)
        income = BigDecimal(0)
        expense = BigDecimal(0)
    }

    @SuppressLint("SetTextI18n")
    private fun resizeText() {
        //Resize text in views if value is huge
        if (binding.tvSearchIncomeNum.text.length > 13
            || binding.tvSearchExpenseNum.text.length > 13
            || binding.tvSearchTotalNum.text.length > 13
        ) {
            binding.tvSearchIncomeNum.textSize = 11F
            binding.tvSearchExpenseNum.textSize = 11F
            binding.tvSearchTotalNum.textSize = 11F
        } else {
            binding.tvSearchIncomeNum.textSize = 18F
            binding.tvSearchExpenseNum.textSize = 18F
            binding.tvSearchTotalNum.textSize = 18F
        }
    }

    private fun initAutoCompleteEditText() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listNote.distinct()
        )
        binding.edSearchNote.setAdapter(adapter)
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}