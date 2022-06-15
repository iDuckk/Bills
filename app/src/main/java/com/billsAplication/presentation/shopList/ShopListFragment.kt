package com.billsAplication.presentation.shopList

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentShopListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.shopList.ShopListAdapter
import com.billsAplication.presentation.addNote.AddNoteFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject


class ShopListFragment : Fragment() {

    private var _binding: FragmentShopListBinding? = null
    private val binding: FragmentShopListBinding get() = _binding!!

    @Inject
    lateinit var viewModel: ShopListViewModel

    @Inject
    lateinit var noteAdapter: ShopListAdapter


    private val TYPE_NOTE = 3
    private val ADD_NOTE_KEY = "add_note_key"
    private val ITEM_NOTE_KEY = "item_note_key"
    private val CREATE_TYPE = 10
    private val UPDATE_TYPE = 20

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
        _binding = FragmentShopListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()

        addButtons()

        buttonKeyboard()

        initRecView()

        viewModel.list.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it.toList())
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.clearBackStack("")
                    findNavController().navigate(R.id.action_shopListFragment_to_billsListFragment)
                }
            }
        )
    }

    private fun buttonKeyboard() {
        val bundle = Bundle().apply {
            putInt(ADD_NOTE_KEY, CREATE_TYPE)
        }
        binding.buttonAddNoteKeyboard.setOnClickListener {
            findNavController().navigate(R.id.action_shopListFragment_to_addNoteFragment, bundle)
        }
    }

    private fun initRecView() {
        with(binding.recViewShopList) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noteAdapter
            itemAnimator = null

        }

        noteAdapter.onClickListenerShopListItem = {
            val bundle = Bundle().apply {
                putInt(ADD_NOTE_KEY, UPDATE_TYPE)
                putInt(ITEM_NOTE_KEY, it.id)
            }
            findNavController().navigate(R.id.action_shopListFragment_to_addNoteFragment, bundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("ResourceType")
    private fun addButtons() {
        binding.buttonAddNoteMicro.visibility = View.GONE
        binding.buttonAddNoteKeyboard.visibility = View.GONE

        binding.buttonAddNote.setOnClickListener {
            if (!binding.buttonAddNoteMicro.isVisible) {
                val colorState = ColorStateList
                    .valueOf(
                        requireContext()
                            .getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)
                    )
                binding.buttonAddNoteMicro.visibility = View.VISIBLE
                binding.buttonAddNoteKeyboard.visibility = View.VISIBLE
                binding.buttonAddNote.size = FloatingActionButton.SIZE_MINI
                binding.buttonAddNote.setImageResource(R.drawable.ic_close)
                binding.buttonAddNote.backgroundTintList = colorState
            } else {
                val colorState = ColorStateList
                    .valueOf(
                        requireContext()
                            .getColorFromAttr(com.google.android.material.R.attr.colorSecondary)
                    )

                binding.buttonAddNoteMicro.visibility = View.GONE
                binding.buttonAddNoteKeyboard.visibility = View.GONE
                binding.buttonAddNote.size = FloatingActionButton.SIZE_AUTO
                binding.buttonAddNote.setImageResource(R.drawable.ic_add)
                binding.buttonAddNote.backgroundTintList = colorState
            }

        }
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}