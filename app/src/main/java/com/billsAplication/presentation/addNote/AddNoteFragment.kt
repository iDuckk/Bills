package com.billsAplication.presentation.addNote

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddNoteBinding
import com.billsAplication.utils.InterfaceMainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding: FragmentAddNoteBinding get() = _binding!!

    @Inject
    lateinit var viewModel: AddNoteViewModel

    private val NOTE_KEY = "note"
    private val ADD_NOTE_KEY = "add_note_key"
    private val ITEM_NOTE_KEY = "item_note_key"
    private val CREATE_TYPE_NOTE = 10
    private val UPDATE_TYPE = 20
    private val EMPTY_STRING = ""
    private val COLOR_NOTE_BLUE = "blue"
    private val COLOR_NOTE_RED = "red"
    private val COLOR_NOTE_ORANGE = "orange"
    private val COLOR_NOTE_GREEN = "green"
    private val COLOR_NOTE_YELLOW = "yellow"
    private val COLOR_NOTE_PURPLE = "purple"
    private val COLOR_NOTE_PRIMARY = ""
    private var scope = CoroutineScope(Dispatchers.Main)

    private var COLOR_NOTE = ""

    private val mainActivity by lazy {
        (context as InterfaceMainActivity)
    }

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

        (context as InterfaceMainActivity).navBottom().visibility = View.GONE

        buttonCancel()

        typeMode()

        noteColors()

        val note = arguments?.getString(NOTE_KEY)
        if(!note.isNullOrEmpty()) {
            binding.etAddNote.setText(note)
            scope.launch {
                mainActivity.splash()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun noteColors() {
        binding.noteColorBlue.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_a100))
            COLOR_NOTE = COLOR_NOTE_BLUE
        }
        binding.noteColorRed.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_a100))
            COLOR_NOTE = COLOR_NOTE_RED
        }
        binding.noteColorYellow.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow_600))
            COLOR_NOTE = COLOR_NOTE_YELLOW
        }
        binding.noteColorGreen.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_a200))
            COLOR_NOTE = COLOR_NOTE_GREEN
        }
        binding.noteColorOrange.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_600))
            COLOR_NOTE = COLOR_NOTE_ORANGE
        }
        binding.noteColorPurple.setOnClickListener {
            binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.deep_purple_a100))
            COLOR_NOTE = COLOR_NOTE_PURPLE
        }
        binding.noteColorOnPrimary.setOnClickListener {
            binding.scrollNote.setBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
            COLOR_NOTE = COLOR_NOTE_PRIMARY
        }
    }

    private fun typeMode() {
        when (arguments?.getInt(ADD_NOTE_KEY)) {
            CREATE_TYPE_NOTE -> addItemMode()
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
            COLOR_NOTE_BLUE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_a100))
            COLOR_NOTE_RED -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_a100))
            COLOR_NOTE_YELLOW -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow_600))
            COLOR_NOTE_GREEN -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_a200))
            COLOR_NOTE_ORANGE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_600))
            COLOR_NOTE_PURPLE -> binding.scrollNote.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.deep_purple_a100))
            COLOR_NOTE_PRIMARY -> binding.scrollNote.setBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
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

    override fun onResume() {
        super.onResume()
        if (!scope.isActive)
            scope = CoroutineScope(Dispatchers.Main)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
        (context as InterfaceMainActivity).navBottom().visibility = View.VISIBLE
        _binding = null
    }
}