package com.billsAplication.presentation.bookmarks

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
import com.billsAplication.databinding.FragmentBookmarksBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.BillsAdapter
import com.billsAplication.presentation.adapter.BookmarksAdapter
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding: FragmentBookmarksBinding get() = _binding!!

    private val bundle = Bundle()

    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val BOOKMARK_TYPE = 102

    private var deleteItem = false
    private var listDeleteItems: ArrayList<BillsItem> = ArrayList()

    @Inject
    lateinit var viewModel: BookmarksViewModel

    @Inject
    lateinit var bookmarkAdapter: BookmarksAdapter

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
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE

        binding.imBookmarksBack.setOnClickListener {
            findNavController().navigate(
                R.id.action_bookmarksFragment_to_billsListFragment,
            )
        }

        initRecView()

        viewModel.list.observe(requireActivity()){
            bookmarkAdapter.submitList(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecView() {
        with(binding.recViewBookmarks) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bookmarkAdapter
            binding.recViewBookmarks.itemAnimator = null
        }

        bookmarkAdapter.onClickListenerBookmarkItem = { //TODO
            bundle.putInt(ADD_BILL_KEY, BOOKMARK_TYPE)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_bookmarksFragment_to_addBillFragment, bundle)
        }

        bookmarkAdapter.onLongClickListenerBookmarkItem = { //TODO
            //listDeleteItems = it
        }
        //Highlight item
        bookmarkAdapter.isHighlight.observe(requireActivity()) { //TODO
//            deleteItem = it
//            if (it) {
//                deleteItem = it
//                binding.buttonAddBill.setImageResource(R.drawable.ic_delete_forever)
//                binding.cardViewBar.visibility = View.GONE
//                binding.cardViewBudget.visibility = View.GONE
//                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
//                    View.GONE
//            } else {
//                binding.buttonAddBill.setImageResource(R.drawable.ic_add)
//                binding.cardViewBar.visibility = View.VISIBLE
//                binding.cardViewBudget.visibility = View.VISIBLE
//                (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility =
//                    View.VISIBLE
//            }
        }
    }

}