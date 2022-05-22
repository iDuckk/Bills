package com.billsAplication.presentation.search

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentSearchBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import com.billsAplication.presentation.chooseCategory.ChooseCategoryDialog
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    @Inject
    lateinit var viewModel: SearchViewModel

    @Inject
    lateinit var billAdapter: BillsAdapter

    private val bundle = Bundle()
    private val TYPE_CATEGORY = 2
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
    private val listNote: ArrayList<String> = ArrayList()
    private val EMPTY_STRING = ""

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
    private var chosenItemsCategory = booleanArrayOf()


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

        viewModel.list.observe(requireActivity()) { list ->

            setAmountBar(list)

            list.forEach {
                //Create list of Notes
                if (listNote.isEmpty())
                    if (it.note != EMPTY_STRING)
                        listNote.add(it.note)
                //Create Category list
                if (it.type == TYPE_CATEGORY)
                    categoryList += it.category
                //Create full list
                if (it.type != TYPE_CATEGORY)
                    allItemList.add(it)
            }
            //TODO Думаю надо получить номер дня типо от 1990 года и по нему сортить
            setListAdapter(allItemList)
        }

        titleBar()

        titleAmount()

        searchViews()

        initRecView()

        buttonDelete()

        initAutoCompleteEditText()
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

    }

    private fun amountMax() {
        binding.etSearchAmountMax.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })

        binding.etSearchAmountMax.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Resize text in views if value is huge
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
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })

        binding.etSearchAmountMin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Resize text in views if value is huge
                if (binding.etSearchAmountMin.text!!.length > 13
                ) {
                    binding.etSearchAmountMin.textSize = 12F
                } else {
                    binding.etSearchAmountMin.textSize = 18F
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun categoryView() {
        binding.etSearchCategory.setOnFocusChangeListener { view, b ->
            if (binding.etSearchCategory.isFocused) {
                setChosenItemsDialog() // Set list chosen items
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

    private fun setChosenItemsDialog() {
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
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        val list: ArrayList<BillsItem> = allItemList.clone() as ArrayList<BillsItem>
        val listCategory = binding.etSearchCategory.text.split(", ").toTypedArray()
        //List Note sorting
        if (binding.edSearchNote.text.isNotEmpty())
            allItemList.forEach {
                if (it.note != binding.edSearchNote.text.toString())
                    list.remove(it)
            }
        // List Category
        if(binding.etSearchCategory.text.isNotEmpty())
            allItemList.forEach { item ->
                listCategory.forEach {
                    if (item.category != it)
                        list.remove(item)
                }
            }
        //  List Min View
        if (binding.etSearchAmountMin.text!!.isNotEmpty())
            allItemList.forEach { item ->
                    if (item.amount.replace(",", "").toInt()
                        < binding.etSearchAmountMin.text.toString().replace(",", "").toInt())
                        list.remove(item)
            }
        //  List Max View
        if (binding.etSearchAmountMax.text!!.isNotEmpty())
            allItemList.forEach { item ->
                if (item.amount.replace(",", "").toInt()
                    > binding.etSearchAmountMax.text.toString().replace(",", "").toInt())
                    list.remove(item)
            }

        setListAdapter(list)
    }

    private fun setListAdapter(listSortsCategory: ArrayList<BillsItem>) {
        billAdapter.submitList(listSortsCategory.sortedByDescending { item -> item.date }
            .toList())
        setAmountBar(listSortsCategory)
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
        billAdapter.isHighlight.observe(requireActivity()) {
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
            //Create list of Notes
            if (it.note != EMPTY_STRING)
                listNote.add(it.note)
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
        listNote.forEach {
            Log.w("TAG", it.toString())
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listNote
        )
        binding.edSearchNote.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}