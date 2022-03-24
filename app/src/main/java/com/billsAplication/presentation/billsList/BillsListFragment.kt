package com.billsAplication.presentation.billsList

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.presentation.adapter.BillsAdapter
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding : FragmentBillsListBinding get() = _binding!!

    private val bundle = Bundle()
    @Inject
    lateinit var viewModel: BillsListViewModel
    @Inject
    lateinit var billAdapter: BillsAdapter

    val ADD_BILL_KEY = "add_bill_key"

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

        binding.tvMonth.text = viewModel.currentDate() //Set month`s text in bar
        binding.tvMonth.setOnClickListener { binding.tvMonth.text = viewModel.currentDate() }

        binding.imBackMonth.setOnClickListener{
            binding.tvMonth.text = viewModel.changeMonthBar(false) //Set month`s text in bar
        }

        binding.imNextMonth.setOnClickListener{
            binding.tvMonth.text = viewModel.changeMonthBar(true) //Set month`s text in bar
        }

        binding.buttonAddBill.setOnClickListener {
            bundle.putBoolean(ADD_BILL_KEY, true)
            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
        }

        initRecView()

        viewModel.getAll()

        viewModel.list.observe(requireActivity()){
            billAdapter.submitList(it.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initRecView(){
        with(binding.recViewBill){
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = billAdapter

        }
    }

}