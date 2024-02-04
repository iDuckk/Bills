package com.billsAplication.presentation.shopList

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentShopListBinding
import com.billsAplication.extension.getColorFromAttr
import com.billsAplication.presentation.shopList.dialog.ListenerDialog
import com.billsAplication.presentation.shopList.view.NoteItem
import com.billsAplication.utils.InterfaceMainActivity
import com.billsAplication.utils.StateColorButton
import java.util.Locale
import javax.inject.Inject


class ShopListFragment : Fragment() {

    private var _binding: FragmentShopListBinding? = null
    private val binding: FragmentShopListBinding get() = _binding!!
    @Inject
    lateinit var viewModel: ShopListViewModel
    @Inject
    lateinit var stateColorButton: StateColorButton

    private val ADD_NOTE_KEY = "add_note_key"
    private val KEY_NOTE_RECEIVE = "key_note_receive"
    private val TYPE_NOTE_RECEIVE = "type_note_receive"
    private val TYPE_EQUALS = 2
    private var TYPE_BILL = "type_bill"
    private val NOTE_KEY = "note"
    private val CREATE_TYPE_NOTE = 10
    private val RECORD_AOUDIO_REQUEST = 110
    private val COLOR_NOTE_PRIMARY = ""
    private var type = 0
    private var speechRecognizer: SpeechRecognizer? = null

    private val spacerType = 10.dp
    private val spacerTitle = 5.dp

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
        _binding = FragmentShopListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getType()
        //if we receive note string from other app
        intentActionSendText()
        binding.composeView.setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    ShopListScreen()
                }
            }
        }
    }

    @Composable
    private fun ShopListScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NotesList()
            AddButton()
        }
    }

    @Composable
    private fun BoxScope.AddButton() {
        var visibleButtons by remember { mutableStateOf(false) }
        val gradient = stateColorButton.gradientButton(type)
        val color = stateColorButton.colorButtons(type)
        val gradientBrush = Brush.horizontalGradient(
            colors = listOf(
                colorResource(R.color.text_income),
                colorResource(R.color.text_expense)
            ), // Gradient colors
            startX = gradient[0], // Starting Y position of the gradient
            endX = gradient[1] // Ending Y position of the gradient
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 12.dp, end = 2.dp)
                .align(Alignment.BottomEnd)
                .clip(shape = CircleShape)
                .clickable(onClick = {
                    visibleButtons = !visibleButtons
                })
                .size(56.dp)
                .background(brush = gradientBrush),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(id = R.string.choose_type_of_add_note),
                    tint = Color(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (visibleButtons) 45f else 0f)
                )
            }
        )

        if (visibleButtons) {
            ButtonSpeechToText(color = color)
            ButtonKeyboardToText(color = color)
        }
    }

    @Composable
    private fun NotesList() {
        val notes = viewModel.notes().collectAsState(initial = listOf())
        LazyColumn(content = {
            items(items = notes.value) {
                NoteItem(note = it, bigDp = spacerType) {
                    //TODO
                    findNavController().navigate(R.id.action_shopListFragment_to_addNoteFragment, bundleOf(ADD_NOTE_KEY to CREATE_TYPE_NOTE))
                }
            }
        })
    }

    @Composable
    private fun BoxScope.ButtonKeyboardToText(color: Int) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 78.dp, end = 10.dp)
                .align(Alignment.BottomEnd)
                .clip(shape = CircleShape)
                .clickable {
                    //TODO
                    findNavController().navigate(
                        R.id.action_shopListFragment_to_addNoteFragment,
                        bundleOf(ADD_NOTE_KEY to CREATE_TYPE_NOTE)
                    )
                }
                .size(40.dp)
                .background(color = Color(color)),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_keyboard),
                    contentDescription = stringResource(id = R.string.add_note_with_keyboard),
                    tint = Color(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)),
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    }

    @Composable
    private fun BoxScope.ButtonSpeechToText(color: Int) {
        val openListenerDialog = remember { mutableStateOf(false) }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizerListener()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 20.dp, end = 68.dp)
                .align(Alignment.BottomEnd)
                .clip(shape = CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            openListenerDialog.value = true
                            //Get permission
                            recordPermission()
                            //ColorState of buttons
                            setEnabledNavBottomWhenRec(false)
                            //Start recording
                            speechRecognizer?.startListening(speechRecognizerIntent)
                        },
                        onTap = {
                            openListenerDialog.value = false
                            //Stop recording
                            speechRecognizer?.stopListening()
                            //ColorState of buttons
                            setEnabledNavBottomWhenRec(true)
                        }
                    )
                }
                .size(40.dp)
                .background(color = Color(color)),
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = stringResource(id = R.string.add_note_with_microphone),
                    tint = Color(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)),
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        if (openListenerDialog.value) {
            ListenerDialog(text = stringResource(id = R.string.title_dialog_rec_listener)) {
                openListenerDialog.value = false
            }
        }
    }

    private fun intentActionSendText() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeAction = sharedPref.getBoolean(TYPE_NOTE_RECEIVE, false)
        val note = sharedPref.getString(KEY_NOTE_RECEIVE, "")
        if (typeAction) {
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
                viewModel.addNote(textNote = data!![0], color = COLOR_NOTE_PRIMARY)

            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    private fun recordPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AOUDIO_REQUEST
            )
        }
    }

    private fun getType() {
        //get saved type
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        type = sharedPref.getInt(TYPE_BILL, TYPE_EQUALS)
    }

    private fun setEnabledNavBottomWhenRec(enabled: Boolean) {
        if (enabled) {
            if (requireActivity() is InterfaceMainActivity) {
                mainActivity.navBottom().isEnabled = true
            }
        } else {
            if (requireActivity() is InterfaceMainActivity) {
                mainActivity.navBottom().isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        _binding = null
    }
}