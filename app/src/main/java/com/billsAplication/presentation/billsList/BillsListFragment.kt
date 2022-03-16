package com.billsAplication.presentation.billsList

import android.content.ClipData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.R
import com.billsAplication.data.room.repository.BillsListRepositoryImpl
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter

//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class BillsListFragment : Fragment() {

//    private var param1: String? = null
//    private var param2: String? = null
    private var _binding: FragmentBillsListBinding? = null
    private val binding : FragmentBillsListBinding get() = _binding!!
    lateinit var billAdapter : BillsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBillsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = BillsListRepositoryImpl(requireActivity().application)
        val ITEMS : LiveData<List<BillsItem>> = repo.getAllDataList()
        billAdapter = BillsAdapter()

        initRecView()

        ITEMS.observe(requireActivity()) {
            billAdapter.submitList(it)
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

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment BillsListFragment.
//         */
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            BillsListFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}