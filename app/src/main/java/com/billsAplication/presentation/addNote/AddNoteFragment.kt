package com.billsAplication.presentation.addNote

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddNoteBinding
import com.billsAplication.databinding.FragmentSearchBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.presentation.search.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding: FragmentAddNoteBinding get() = _binding!!

    @Inject
    lateinit var viewModel: AddNoteViewModel

    private val TYPE_NOTE = 3
    private val ADD_NOTE_KEY = "add_note_key"
    private val ITEM_NOTE_KEY = "item_note_key"
    private val CREATE_TYPE = 10
    private val UPDATE_TYPE = 20
    private val EMPTY_STRING = ""
    private val COLOR_NOTE_BLUE = "blue"
    private val COLOR_NOTE_RED = "red"
    private val COLOR_NOTE_ORANGE = "orange"
    private val COLOR_NOTE_GREEN = "green"
    private val COLOR_NOTE_YELLOW = "yellow"
    private val COLOR_NOTE_PURPLE = "purple"
    private val COLOR_NOTE_PRIMARY = ""

    private var COLOR_NOTE = ""

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
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .visibility = View.GONE

        buttonCancel()

        typeMode()

        noteColors()
    }

    @SuppressLint("ResourceAsColor")
    private fun noteColors() {
        binding.noteColorBlue.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
            COLOR_NOTE = COLOR_NOTE_BLUE
        }
        binding.noteColorRed.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_coral))
            COLOR_NOTE = COLOR_NOTE_RED
        }
        binding.noteColorYellow.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.khaki))
            COLOR_NOTE = COLOR_NOTE_YELLOW
        }
        binding.noteColorGreen.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_yellow))
            COLOR_NOTE = COLOR_NOTE_GREEN
        }
        binding.noteColorOrange.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.andy_brown))
            COLOR_NOTE = COLOR_NOTE_ORANGE
        }
        binding.noteColorPurple.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.plum))
            COLOR_NOTE = COLOR_NOTE_PURPLE
        }
        binding.noteColorOnPrimary.setOnClickListener {
            binding.scrollNote.setBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            COLOR_NOTE = COLOR_NOTE_PRIMARY
        }
    }

    private fun typeMode() {
        when (arguments?.getInt(ADD_NOTE_KEY)) {
            CREATE_TYPE -> addItemMode()
            UPDATE_TYPE -> updateItemMode()
        }
    }

    private fun addItemMode() {
        binding.bDeleteNote.isEnabled = false

        binding.bAddNote.setOnClickListener {
            if(binding.etAddNote.text.toString() != EMPTY_STRING) {
                CoroutineScope(IO).launch {
                    viewModel.add(binding.etAddNote.text.toString(), COLOR_NOTE)
                }
                findNavController().navigate(R.id.action_addNoteFragment_to_shopListFragment)
            }
        }

    }

    private fun updateItemMode() {
        CoroutineScope(Main).launch {
            binding.etAddNote.setText(viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY)).note)
            //Color
            COLOR_NOTE = viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY)).description
            setColorNote(COLOR_NOTE)
        }

        binding.bDeleteNote.setOnClickListener {
            CoroutineScope(IO).launch {
                viewModel.delete(viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY)))
            }
            findNavController().navigate(R.id.action_addNoteFragment_to_shopListFragment)
        }

        binding.bAddNote.setOnClickListener {
            CoroutineScope(IO).launch {
                if(viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY)).note
                    != binding.etAddNote.text.toString()
                    || viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY)).description
                != COLOR_NOTE){
                    val item = viewModel.getItem(requireArguments().getInt(ITEM_NOTE_KEY))
                    viewModel.updateNotesList(
                        viewModel.newItem(item.id, binding.etAddNote.text.toString(), COLOR_NOTE)
                    )
                }
            }
            findNavController().navigate(R.id.action_addNoteFragment_to_shopListFragment)

        }
    }

    private fun buttonCancel() {
        binding.bCancelNote.setOnClickListener {
            findNavController().navigate(R.id.action_addNoteFragment_to_shopListFragment)
        }
    }

    private fun setColorNote(colorNote: String){
        when(colorNote){
            COLOR_NOTE_BLUE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sky_blue))
            COLOR_NOTE_RED -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_coral))
            COLOR_NOTE_YELLOW -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.khaki))
            COLOR_NOTE_GREEN -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_yellow))
            COLOR_NOTE_ORANGE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.andy_brown))
            COLOR_NOTE_PURPLE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.plum))
            COLOR_NOTE_PRIMARY -> binding.scrollNote.setBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        }
    }

    @ColorInt
    fun Context.getColorFromAttr(@AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .visibility = View.VISIBLE
        _binding = null }

}