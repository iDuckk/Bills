package com.billsAplication.presentation.billsList

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.exp


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding: FragmentBillsListBinding get() = _binding!!

    private val bundle = Bundle()

    @Inject
    lateinit var viewModel: BillsListViewModel

    @Inject
    lateinit var billAdapter: BillsAdapter

    var income = BigDecimal(0)
    var expense = BigDecimal(0)
    private var deleteItem = false
    private var listDeleteItems: ArrayList<BillsItem> = ArrayList()

    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE

        binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
        binding.tvMonth.setOnClickListener {
            viewModel.currentDate
            binding.tvMonth.text = viewModel.currentDate()
            viewModel.defaultMonth()
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.imBackMonth.setOnClickListener { //Previous month
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.imNextMonth.setOnClickListener { //Next month
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

        binding.buttonAddBill.setOnClickListener {
            if (deleteItem) {
                CoroutineScope(Main).launch {
                    if (listDeleteItems.isNotEmpty()) {
                        listDeleteItems.forEach {
                            viewModel.delete(it)
                        }
                    }
                    billAdapter.deleteItems()
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

        initRecView()
        //set list of month
        viewModel.list.observe(requireActivity()) {
            //Add list in adapter
            billAdapter.submitList(it.sortedBy { item -> item.date }.toList())
            //Create amount for title amountTextView
            it.forEachIndexed { index, item ->
                when (item.type) {
                    TYPE_INCOME ->
                        income += BigDecimal(item.amount.replace(",", ""))
                    TYPE_EXPENSES ->
                        expense += BigDecimal(item.amount.replace(",", ""))
                }

                if (it.lastIndex == index && _binding != null)
                    setAmountNum()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        //set a new list
        viewModel.list.removeObservers(this)
        viewModel.getMonth(month)
        viewModel.list.observe(requireActivity()) {
            billAdapter.submitList(it.toList())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAmountNum() {
        billAdapter.currentList.forEachIndexed { index, item ->
            binding.tvIncomeNum.text = "%,.2f".format(income)
            binding.tvExpenseNum.text = "%,.2f".format(expense)
            binding.tvTotalNum.text = "%,.2f".format((income - expense))
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
        income = BigDecimal(0)
        expense = BigDecimal(0)
    }

}