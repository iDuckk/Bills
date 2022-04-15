package com.billsAplication.presentation.billsList

import android.os.Build
import android.os.Bundle
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
import com.billsAplication.presentation.adapter.BillsAdapter
import javax.inject.Inject


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding : FragmentBillsListBinding get() = _binding!!

    private val bundle = Bundle()
    @Inject
    lateinit var viewModel: BillsListViewModel
    @Inject
    lateinit var billAdapter: BillsAdapter

    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"

    private val NEXT_MONTH = true
    private val PREV_MONTH = false
    private val CREATE_TYPE = true
    private val UPDATE_TYPE = false

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
        binding.tvMonth.setOnClickListener {
            viewModel.currentDate
            binding.tvMonth.text = viewModel.currentDate()
            viewModel.defaultMonth()
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.imBackMonth.setOnClickListener{ //Previous month
            viewModel.currentDate = viewModel.changeMonthBar(PREV_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.imNextMonth.setOnClickListener{ //Next month
            viewModel.currentDate = viewModel.changeMonthBar(NEXT_MONTH)
            binding.tvMonth.text = viewModel.currentDate //Set month`s text in bar
            //set a new list
            setNewList(binding.tvMonth.text.toString())
        }

        binding.buttonAddBill.setOnClickListener {
            bundle.putBoolean(ADD_BILL_KEY, CREATE_TYPE)
            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
        }

        initRecView()
        //set list of month
        viewModel.list.observe(requireActivity()){
            billAdapter.submitList(it.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecView(){
        with(binding.recViewBill){
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = billAdapter
            binding.recViewBill.itemAnimator = null
            //billAdapter.currentList.sortByDescending { it.date.toInt() }
        }


        billAdapter.onClickListenerBillItem = {
            bundle.putBoolean(ADD_BILL_KEY, UPDATE_TYPE)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNewList(month: String){
        //set a new list
        viewModel.list.removeObservers(this)
        viewModel.getMonth(month)
        viewModel.list.observe(requireActivity()){
            billAdapter.submitList(it.toList())
        }
    }

}