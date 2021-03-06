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
import com.billsAplication.presentation.adapter.bookmarks.BookmarksAdapter
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        binding.imBookmarksDelete.visibility = View.GONE

        binding.imBookmarksBack.setOnClickListener {
            findNavController().navigate(
                R.id.action_bookmarksFragment_to_billsListFragment,
            )
        }

        binding.imBookmarksDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (listDeleteItems.isNotEmpty()) {
                    listDeleteItems.forEach {
                        viewModel.updateBookmarks(billItemMapper(it))
                    }
                }
                bookmarkAdapter.deleteItemsAfterRemovedItemFromDB()
                deleteItem = false
                listDeleteItems.clear()
            }
        }

        initRecView()

        viewModel.list.observe(viewLifecycleOwner){
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

        bookmarkAdapter.onClickListenerBookmarkItem = {
            bundle.putInt(ADD_BILL_KEY, BOOKMARK_TYPE)
            bundle.putParcelable(BILL_ITEM_KEY, it)
            findNavController().navigate(R.id.action_bookmarksFragment_to_addBillFragment, bundle)
        }

        bookmarkAdapter.onLongClickListenerBookmarkItem = {
            listDeleteItems = it
        }
        //Highlight item
        bookmarkAdapter.isHighlight.observe(viewLifecycleOwner) {
            deleteItem = it
            if (it) {
                deleteItem = it
                binding.imBookmarksDelete.visibility = View.VISIBLE
            } else {
                binding.imBookmarksDelete.visibility = View.GONE
            }
        }
    }

    private fun billItemMapper(item: BillsItem): BillsItem{
        //change Bookmark
        return BillsItem(
            item.id,
            item.type,
            item.month,
            item.date,
            item.time,
            item.category,
            item.amount,
            item.note,
            item.description,
            false,
            item.image1,
            item.image2,
            item.image3,
            item.image4,
            item.image5
        )
    }

}