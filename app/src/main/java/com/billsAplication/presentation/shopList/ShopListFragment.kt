package com.billsAplication.presentation.shopList

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentShopListBinding
import com.billsAplication.presentation.adapter.shopList.ShopListAdapter
import com.billsAplication.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mobile.ads.banner.AdSize
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
    private val KEY_NOTE_RECEIVE = "key_note_receive"
    private val TYPE_NOTE_RECEIVE = "type_note_receive"
    private val TYPE_EQUALS = 2
    private var TYPE_BILL = "type_bill"
    private val NOTE_KEY = "note"
    private val CREATE_TYPE_NOTE = 10
    private val UPDATE_TYPE = 20
    private val RECORD_AOUDIO_REQUEST = 110
    private val COLOR_NOTE_PRIMARY = ""
    private val TAG = "ShopListFragment"
    private var buttonMotion = true
    private var type = 0
    lateinit var dialogRecording: AlertDialog
    private var speechRecognizer: SpeechRecognizer? = null
    private var scope = CoroutineScope(Dispatchers.Main)

    private val mainActivity by lazy {
        (context as InterfaceMainActivity)
    }

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

        getType()

        addButtons()

        buttonKeyboard()

        buttonSpeechToText()

        initRecView()
        //if we receive note string from other app
        intentActionSendText()

        viewModel.list.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it.toList())
        }
    }

    private fun intentActionSendText() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeAction = sharedPref.getBoolean(TYPE_NOTE_RECEIVE, false)
        val note = sharedPref.getString(KEY_NOTE_RECEIVE, "")
        if(typeAction){
            bundleOf(NOTE_KEY to note, ADD_NOTE_KEY to CREATE_TYPE_NOTE).let {
                setSharePrefSetActionFalse()
                findNavController().navigate(R.id.action_shopListFragment_to_addNoteFragment, it)
            }
        }
    }

    private fun setSharePrefSetActionFalse() {
        //Save statement of Currency in Share preference
        val sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(TYPE_NOTE_RECEIVE, false)
            apply()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun buttonSpeechToText() {
//        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        speechIntent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak please")
//        startActivityForResult(speechIntent, 1) //RESULT_SPEECH_TO_TEXT

//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            if (requestCode == RESULT_SPEECH_TO_TEXT && resultCode == RESULT_OK) {
//                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                TextView tvSpeech = (TextView) findViewById(R.id.tv_speech);
//                tvSpeech.setText(matches.get(0));
//            }
//        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

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
            putInt(ADD_NOTE_KEY, CREATE_TYPE_NOTE)
        }
        binding.buttonAddNoteKeyboard.setOnClickListener {
            buttonMotion = true
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
        setColorStateAddButtons()
        //Size AddButton
        val bSize = requireContext()
            .resources.getDimensionPixelSize(com.google.android
                .material.R.dimen.design_fab_size_normal)
        //Size Nav Bottom
        val nbSize = requireContext()
            .resources.getDimensionPixelSize(com.google.android
                .material.R.dimen.design_bottom_navigation_height)
        //Size of Ad
        var adSize = AdSize.BANNER_320x50.height.toDp(requireContext())
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels
        val marginTop = (screenHeight - adSize - nbSize - bSize - (screenHeight * 5 / 100))
        binding.guidelineAddNote.setGuidelineBegin(marginTop)

        binding.buttonAddNote.mainLayout.setOnClickListener {
            val rectf = Rect()
            binding.buttonAddNote.root.getGlobalVisibleRect(rectf)
            //Get pixels for button's motion
            val marginX = -(screenWidth - rectf.left)
            val marginY = -(bSize / 2 + (screenHeight * 5 / 100))

            if (buttonMotion) {
                rotationView(requireView().findViewById<ImageView>(R.id.imageButton), 0f, 45f)
                motionViewY(
                    requireView().findViewById<ImageView>(R.id.button_addNote_micro),
                    0f,
                    marginY.toFloat()
                )
                motionViewX(
                    requireView().findViewById<ImageView>(R.id.button_addNote_keyboard),
                    0f,
                    marginX.toFloat()
                )
                buttonMotion = false
            } else {
                rotationView(requireView().findViewById<ImageView>(R.id.imageButton), 45f, 0f)
                motionViewY(
                    requireView().findViewById<ImageView>(R.id.button_addNote_micro),
                    marginY.toFloat(),
                    0f
                )
                motionViewX(
                    requireView().findViewById<ImageView>(R.id.button_addNote_keyboard),
                    marginX.toFloat(),
                    0f
                )
                buttonMotion = true
            }
        }
    }

    private fun getType(){
        //get saved type
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        type = sharedPref.getInt(TYPE_BILL, TYPE_EQUALS)
    }

    private fun setColorStateAddButtons() {
        val colorState = ColorStateList
            .valueOf(stateColorButton.colorButtons(type))
        binding.buttonAddNoteMicro.backgroundTintList = colorState
        binding.buttonAddNoteKeyboard.backgroundTintList = colorState
        binding.buttonAddNote.relativeLayout.background = stateColorButton.colorAddButton(type)
        binding.buttonAddNoteMicro.size = FloatingActionButton.SIZE_MINI
        binding.buttonAddNoteKeyboard.size = FloatingActionButton.SIZE_MINI
    }

    private fun setEnabledViewsFOrRec(enabled: Boolean) {
        if (enabled) {
            val colorState = ColorStateList
                .valueOf(stateColorButton.colorButtons(type))
            binding.buttonAddNote.mainLayout.visibility = View.VISIBLE
            binding.buttonAddNoteKeyboard.visibility = View.VISIBLE
            if(requireActivity() is InterfaceMainActivity){
                mainActivity.navBottom().isEnabled = true
            }
            binding.buttonAddNoteMicro.backgroundTintList = colorState
            dialogRecording.dismiss()
        } else {
            val colorState = ColorStateList
                .valueOf(stateColorButton.colorButtons(type))
            binding.buttonAddNote.mainLayout.visibility = View.INVISIBLE
            binding.buttonAddNoteKeyboard.visibility = View.INVISIBLE
            if(requireActivity() is InterfaceMainActivity) {
                mainActivity.navBottom().isEnabled = false
            }
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

    // Extension method to convert pixels to dp
    fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    override fun onResume() {
        super.onResume()
        if (!scope.isActive)
            scope = CoroutineScope(Dispatchers.Main)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        scope.cancel()
        _binding = null
    }
}