package com.billsAplication.presentation.billsList

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding: FragmentBillsListBinding get() = _binding!!

    private val bundle = Bundle()

    @Inject
    lateinit var viewModel: BillsListViewModel

    @Inject
    lateinit var billAdapter: BillsAdapter

    private var income = BigDecimal(0)
    private var expense = BigDecimal(0)
    private var deleteItem = false
    private var listDeleteItems: ArrayList<BillsItem> = ArrayList()
    private var titleIncome = MutableLiveData<BigDecimal>()
    private var titleExpense = MutableLiveData<BigDecimal>()
    private var titleTotal = MutableLiveData<BigDecimal>()

    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_FULL_LIST_SORT = 101

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .visibility = View.VISIBLE
        binding.cardViewFilter.visibility = View.GONE

        titleAmount()

        titleBar()

        searchBar()

        addButton()

        initRecView()

        setNewList(binding.tvMonth.text.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addButton() {
        binding.buttonAddBill.setOnClickListener {
            if (deleteItem) {
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
            } else {
                bundle.putInt(ADD_BILL_KEY, CREATE_TYPE)
                findNavController().navigate(
                    R.id.action_billsListFragment_to_addBillFragment,
                    bundle
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchBar(){
        var incomeList = ArrayList<BillsItem>()
        var expenseList = ArrayList<BillsItem>()
        //income list
        //binding.cardViewFilter.visibility = View.VISIBLE
        binding.checkBoxIncome.setOnClickListener {
            //Set Default value spinner
            binding.spinnerFilter.setSelection(0)

            if(binding.checkBoxIncome.isChecked){
                incomeList = getSortList(TYPE_INCOME)

                if(binding.checkBoxExpense.isChecked) {
                    val fullList = ArrayList<BillsItem>()
                    fullList.addAll(incomeList)
                    fullList.addAll(expenseList)
                    setDescentSorting(fullList)
                }else
                    setDescentSorting(incomeList)
            }else {
                if(!binding.checkBoxExpense.isChecked) {
                    setDescentSorting(viewModel.list.value!!)
                }
                else
                    setDescentSorting(expenseList)
                incomeList.clear()
            }
        }
        //Expense list
        binding.checkBoxExpense.setOnClickListener {
            //Set Default value spinner
            binding.spinnerFilter.setSelection(0)

            if(binding.checkBoxExpense.isChecked){
                expenseList = getSortList(TYPE_EXPENSES)

                if (binding.checkBoxIncome.isChecked) {
                    val fullList = ArrayList<BillsItem>()
                    fullList.addAll( expenseList)
                    fullList.addAll(incomeList)
                    setDescentSorting(fullList)
                }else
                    setDescentSorting(expenseList)
            }else {
                if (!binding.checkBoxIncome.isChecked) {
                    setDescentSorting(viewModel.list.value!!)
                }else {
                    setDescentSorting(incomeList)
                }
                expenseList.clear()
            }
        }
        //Descending sort
        binding.checkBoxDecDate.setOnClickListener {
            setDescentSorting(billAdapter.currentList)
        }
        //Set Spinner
        spinnerCategory()

    }
    //Get list Type (Income or Expense)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSortList(type : Int): ArrayList<BillsItem> {
        val list =  ArrayList<BillsItem>()
        viewModel.list.value?.forEach {
            if (it.type == type)
                list.add(it)
        }
        return list
    }
    //Sort list after submit if ArrayList
    private fun setDescentSorting(list: ArrayList<BillsItem>) {
        if (!binding.checkBoxDecDate.isChecked)
            billAdapter.submitList(list.sortedBy { item -> item.date }.toList())
        else
            billAdapter.submitList(list.sortedByDescending { item -> item.date }.toList())
    }
    //Sort list after submit if ViewModel.list.value!!
    private fun setDescentSorting(list: List<BillsItem>) {
        if (!binding.checkBoxDecDate.isChecked)
            billAdapter.submitList(list.sortedBy { item -> item.date }.toList())
        else
            billAdapter.submitList(list.sortedByDescending { item -> item.date }.toList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun spinnerCategory() {
        val listCategory = ArrayList<String>()
        listCategory.add("None") //Default value without it doesn't work

        //create Category List
        viewModel.listCategory.observe(requireActivity()) { list ->
            //Because Observer in ViewModel, and it doesn't destroy.
            //According that we add items again
            if(listCategory.isNotEmpty()){
                listCategory.clear()
                listCategory.add("None")
            }
            list.forEach { item ->
                    listCategory += item.category
            }
        }


        val spinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listCategory
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerFilter.adapter = spinnerAdapter

        // Set an on item selected listener for spinner object
        binding.spinnerFilter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {//parent.getItemAtPosition(position).toString()
                // Display the selected item text on text view
                if(parent.getItemAtPosition(position).toString() != "None") {
                    if(binding.checkBoxIncome.isChecked && !binding.checkBoxExpense.isChecked)
                        setDescentSorting(spinnerItemList(TYPE_INCOME, parent.getItemAtPosition(position).toString()))
                    else if(binding.checkBoxExpense.isChecked && !binding.checkBoxIncome.isChecked)
                        setDescentSorting(spinnerItemList(TYPE_EXPENSES, parent.getItemAtPosition(position).toString()))
                    else
                        setDescentSorting(spinnerItemList(TYPE_FULL_LIST_SORT, parent.getItemAtPosition(position).toString()))
                } else {
                    if(binding.checkBoxIncome.isChecked && !binding.checkBoxExpense.isChecked)
                        setDescentSorting(getSortList(TYPE_INCOME))
                    else if(binding.checkBoxExpense.isChecked && !binding.checkBoxIncome.isChecked)
                        setDescentSorting(getSortList(TYPE_EXPENSES))
                    else
                        setDescentSorting(viewModel.list.value!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {// Another interface callback}
            }
        }
    }
    //Get list if Spinner chosen a Category
    @RequiresApi(Build.VERSION_CODES.O)
    private fun spinnerItemList(type: Int, value: String): ArrayList<BillsItem>{
        val listCat = ArrayList<BillsItem>()
        //If chosen "None"
        if(type == TYPE_FULL_LIST_SORT){
            viewModel.list.value?.forEach {
                if (it.category == value)
                    listCat += it
            }
        }else {
            getSortList(type).forEach {
                if (it.category == value)
                    listCat += it
            }
        }
        return listCat
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun titleBar() {
        //Sorting
        binding.imBillsFilter.setOnClickListener{
            if(binding.cardViewFilter.isVisible) {
                billAdapter.submitList(viewModel.list.value?.sortedBy { it.date }?.toList())
                binding.checkBoxIncome.isChecked = false
                binding.checkBoxExpense.isChecked = false
                binding.checkBoxDecDate.isChecked = false
                binding.spinnerFilter.setSelection(0)
                binding.cardViewFilter.visibility = View.GONE
            }else{
                binding.cardViewFilter.visibility = View.VISIBLE
            }
        }
        //Set month`s text in bar
        binding.tvMonth.text = viewModel.currentDate
        binding.tvMonth.setOnClickListener {
            viewModel.currentDate
            binding.tvMonth.text = viewModel.currentDate()
            viewModel.defaultMonth()
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }
        //Previous month
        binding.imBackMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }
        //Next month
        binding.imNextMonth.setOnClickListener {
            viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.imBookmarks.setOnClickListener {
            findNavController().navigate(
                R.id.action_billsListFragment_to_bookmarksFragment,
            )
        }
    }

    private fun titleAmount() {
        //Set title Total
        titleTotal.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvTotalNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
        //Set title Expense
        titleExpense.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvExpenseNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
        //Set title Income
        titleIncome.observe(requireActivity()) {
            if (_binding != null) {
                binding.tvIncomeNum.text = "%,.2f".format(it)
                resizeText()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            binding.cardViewFilter.visibility = View.GONE
        }
        //Highlight item
        billAdapter.isHighlight.observe(requireActivity()) {
            deleteItem = it
            if (it) {
                deleteItem = it
                binding.buttonAddBill.setImageResource(R.drawable.ic_delete_forever)
                binding.cardViewBar.visibility = View.GONE
                binding.cardViewBudget.visibility = View.GONE
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
                    View.GONE
            } else {
                binding.buttonAddBill.setImageResource(R.drawable.ic_add)
                binding.cardViewBar.visibility = View.VISIBLE
                binding.cardViewBudget.visibility = View.VISIBLE
                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
                    View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNewList(month: String) {
        //Delete observe if Active
        if(viewModel.list.hasActiveObservers())
        viewModel.list.removeObservers(this)
        //set a new list
        viewModel.getMonth(month)
        viewModel.list.observe(requireActivity()) {
            billAdapter.submitList(it.sortedBy { item -> item.date }.toList())
            //Create amount for title amountTextView
            it.forEach { item ->
                when (item.type) {
                    TYPE_INCOME ->
                        income += BigDecimal(item.amount.replace(",", ""))
                    TYPE_EXPENSES ->
                        expense += BigDecimal(item.amount.replace(",", ""))
                }
            }
            titleIncome.postValue(income)
            titleExpense.postValue(expense)
            titleTotal.postValue(income - expense)
            income = BigDecimal(0)
            expense = BigDecimal(0)

        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}