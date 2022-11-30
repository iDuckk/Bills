package com.billsAplication.presentation.shopList

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentShopListBinding
import com.billsAplication.presentation.adapter.shopList.ShopListAdapter
import com.billsAplication.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import javax.inject.Inject


class ShopListFragment : Fragment() {

    private var _binding: FragmentShopListBinding? = null
    private val binding: FragmentShopListBinding get() = _binding!!

    @Inject
    lateinit var viewModel: ShopListViewModel
    @Inject
    lateinit var noteAdapter: ShopListAdapter
    @Inject
    lateinit var mToast: mToast
    @Inject
    lateinit var stateColorButton: StateColorButton
    @Inject
    lateinit var rotationView: RotationView
    @Inject
    lateinit var slideView: SlideView
    @Inject
    lateinit var motionViewY: MotionViewY
    @Inject
    lateinit var motionViewX: MotionViewX

    private val ADD_NOTE_KEY = "add_note_key"
    private val ITEM_NOTE_KEY = "item_note_key"
    private val CREATE_TYPE = 10
    private val UPDATE_TYPE = 20
    private val RECORD_AOUDIO_REQUEST = 110
    private val COLOR_NOTE_PRIMARY = ""
    private var buttonMotion = true
    lateinit var dialogRecording: AlertDialog

    private var speechRecognizer: SpeechRecognizer? = null

    private val component by lazy {
        (requireActivity().application as BillsApplication).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        dialogRecording()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addButtons()

        buttonKeyboard()

        buttonSpeechToText()

        initRecView()

        viewModel.list.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it.toList())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizerListener()

        binding.buttonAddNoteMicro.setOnTouchListener { view, motionEvent ->
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //Stop recording
                speechRecognizer?.stopListening()
                //ColorState of buttons
                setEnabledViewsFOrRec(true)
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //Get permission
                recordPermission()
                //ColorState of buttons
                setEnabledViewsFOrRec(false)
                //Start recording
                speechRecognizer?.startListening(speechRecognizerIntent)
                return@setOnTouchListener true
            }
            false
        }
    }

    private fun speechRecognizerListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
//                Log.d("TAG", "Beginning")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                Log.d("TAG", data!![0])
                CoroutineScope(Main).launch {
                    viewModel.add(data!![0], COLOR_NOTE_PRIMARY)

                }
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    private fun recordPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                RECORD_AOUDIO_REQUEST
            )
        }
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

    @SuppressLint("ResourceType")
    private fun addButtons() {
        val layoutParams = ConstraintLayout.LayoutParams(binding.buttonAddNote.root.layoutParams)
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels
        val marginTop = screenHeight - (screenHeight * 21 / 100)
        val marginEnd = screenWidth * 4 / 100
        layoutParams.topToTop = requireView().top
        layoutParams.endToEnd = requireView().right
        layoutParams.marginEnd = marginEnd
        layoutParams.topMargin = marginTop
        binding.buttonAddNote.root.layoutParams = layoutParams

        val colorState = ColorStateList
            .valueOf(stateColorButton.colorButtons!!)
        binding.buttonAddNote.relativeLayout.background = stateColorButton.colorAddButton
        binding.buttonAddNoteMicro.size = FloatingActionButton.SIZE_MINI
        binding.buttonAddNoteKeyboard.size = FloatingActionButton.SIZE_MINI
        binding.buttonAddNoteMicro.backgroundTintList = colorState
        binding.buttonAddNoteKeyboard.backgroundTintList = colorState

        binding.buttonAddNote.mainLayout.setOnClickListener {
            if (buttonMotion) {
                rotationView(requireView().findViewById<ImageView>(R.id.imageButton), 0f, 45f)
                motionViewY(requireView().findViewById<ImageView>(R.id.button_addNote_micro), 0f,  -170f)
                motionViewX(requireView().findViewById<ImageView>(R.id.button_addNote_keyboard), 0f, -200f)
                buttonMotion = false
            } else {
                rotationView(requireView().findViewById<ImageView>(R.id.imageButton), 45f, 0f)
                motionViewY(requireView().findViewById<ImageView>(R.id.button_addNote_micro), -170f, 0f)
                motionViewX(requireView().findViewById<ImageView>(R.id.button_addNote_keyboard),-200f, 0f)
                buttonMotion = true
            }
        }
    }

    private fun setEnabledViewsFOrRec(enabled: Boolean) {
        if (enabled) {
            val colorState = ColorStateList
                .valueOf(
                    requireContext()
                        .getColorFromAttr(com.google.android.material.R.attr.colorSecondary)
                )
            binding.buttonAddNote.mainLayout.visibility = View.VISIBLE
            binding.buttonAddNoteKeyboard.visibility = View.VISIBLE
            (context as InterfaceMainActivity).navBottom().isEnabled = true
            binding.buttonAddNoteMicro.backgroundTintList = colorState
            dialogRecording.dismiss()
        } else {
            val colorState = ColorStateList
                .valueOf(
                    requireContext().getColor(R.color.default_background)
                )
            binding.buttonAddNote.mainLayout.visibility = View.INVISIBLE
            binding.buttonAddNoteKeyboard.visibility = View.INVISIBLE
            (context as InterfaceMainActivity).navBottom().isEnabled = false
            binding.buttonAddNoteMicro.backgroundTintList = colorState
            dialogRecording.show()
        }
    }

    private fun dialogRecording() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        dialogRecording = builder
            .setMessage(getString(R.string.title_dialog_rec_listener))
            .create()
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

    override fun onResume() {
        super.onResume()
        if(!buttonMotion) {
            rotationView(requireView().findViewById<ImageView>(R.id.imageButton), 45f, 0f)
            motionViewY(requireView().findViewById<ImageView>(R.id.button_addNote_micro), -170f, 0f)
            motionViewX(
                requireView().findViewById<ImageView>(R.id.button_addNote_keyboard),
                -200f,
                0f
            )
            buttonMotion = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        _binding = null
    }
}