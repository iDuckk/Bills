package com.billsAplication.presentation.search

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
import com.billsAplication.databinding.FragmentSearchBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private var imageRoll = false
    private var deleteItem = false
    private var allItemList = ArrayList<BillsItem>()
    private var listDeleteItems = ArrayList<BillsItem>()

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

        viewModel.list.observe(requireActivity()){ list ->
            list.forEach {
                if(it.type != TYPE_CATEGORY)
                allItemList.add(it)
            }
            billAdapter.submitList(allItemList)
        }

        titleBar()

        searchViews()

        initRecView()

        buttonDelete()
    }

    private fun buttonDelete() {
        binding.bSearchDelete.setOnClickListener{
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
            billAdapter.submitList(allItemList.toMutableList())
        }
    }

    private fun searchViews(){
        imageRoll()

    }

    private fun imageRoll(){
        binding.imRollViews.setOnClickListener {
            if(imageRoll) {
                binding.imRollViews.setImageResource(R.drawable.ic_arrow_up)
                imageRoll = false
                binding.cardViewSearch.visibility = View.VISIBLE
            }
            else {
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
            layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
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

    private fun setViewsVisibility(b: Boolean){
        if(b) {
            binding.bSearchDelete.visibility = View.GONE
            binding.cardViewSearch.visibility = View.VISIBLE
            binding.cardViewSearchBar.visibility = View.VISIBLE
            binding.cardViewRollImage.visibility = View.VISIBLE
            binding.cardViewSearchBudget.visibility = View.VISIBLE
        }else{
            binding.bSearchDelete.visibility = View.VISIBLE
            binding.cardViewSearch.visibility = View.GONE
            binding.cardViewSearchBar.visibility = View.GONE
            binding.cardViewRollImage.visibility = View.GONE
            binding.cardViewSearchBudget.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}