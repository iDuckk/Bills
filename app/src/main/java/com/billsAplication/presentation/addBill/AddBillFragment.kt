@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.billsAplication.presentation.addBill

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddBillBinding
import com.billsAplication.domain.model.ImageItem
import com.billsAplication.presentation.adapter.*
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategory
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategoryViewModel
import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
class AddBillFragment : Fragment() {

    private var _binding: FragmentAddBillBinding? = null
    private val binding : FragmentAddBillBinding get() = _binding!!

    private val REQUEST_CODE_PERMISSIONS = 100
    private var REQUEST_IMAGE_CAPTURE = 102
    private var PICK_IMAGE = 109
    private val ADD_BILL_KEY = "add_bill_key"
    private val REQUESTKEY_CATEGORY_ITEM = "RequestKey_Category_item"
    private val BUNDLEKEY_CATEGORY_ITEM = "BundleKey_Category_item"
    private val TAG_DIALOG_CATEGORY = "Dialog Category"
    private val TYPE_EXPENSE = 0
    private val TYPE_INCOME = 1
    private val DATE = 0
    private val TIME = 1
    private val CATEGORY = 2
    private val AMOUNT = 3
    private val NOTE = 4
    private val DESCRIPTION = 5

    private val bitmapByte: MutableList<ImageItem> = ArrayList()

    private var ID_IMAGE = 0
    private var photoFile: File? = null
    private var TYPE_BILL = TYPE_EXPENSE
    private var bookmark = false
    private var checkFocus = true

    lateinit var colorState : ColorStateList

    @Inject
    lateinit var viewModel: FragmentDialogCategoryViewModel
    @Inject
    lateinit var imageAdapter: ImageAdapter

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
        _binding = FragmentAddBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set Bottom bar - invisible
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE

        //Set Currency of amount EditText - Default currency
        binding.tvCurrancy.text = DecimalFormat().currency.currencyCode

        //Set autoCompleteEditText
        //initAutoCompleteEditText() //TODO LIST

        imageListeners()

        initRecViewImage()

        textViewListeners()

        editTextListeners()

        binding.bAddSave.setOnClickListener { //Create or Update
            //TODO
//            val f = createImageFile()
//            f.writeBytes(b)
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }

        //Type entrances: Add or Update
        val type = arguments?.getBoolean(ADD_BILL_KEY)
        if(type!!){             //Create item
            //Set Date
            binding.edDateAdd.setText(SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time))
            //Set Time
            binding.edTimeAdd.setText(SimpleDateFormat("HH:mm a").format(Calendar.getInstance().time))
            //Set Expense TextView as default
            binding.tvAddExpenses.performClick()
        }else{                  //Edit item
            //TODO IF EDIT
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun initRecViewImage(){

        imageAdapter.submitList(bitmapByte.toMutableList())

        with(binding.recViewPhoto){
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter

        }
        onClickListenerDeleteImage = {
            bitmapByte.remove(it)
            imageAdapter.submitList(bitmapByte.toMutableList())
        }

        onClickListenerSaveImage = {
            //Save image on storage
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                BitmapFactory.decodeByteArray(it.bytesImage,0, it.bytesImage.size),
                SimpleDateFormat("yyyyMMdd").format(Date()) + "_${it.id}",
                null
            )
        }

        onClickListenerItem = {
            onCreateDialog(it)!!.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imageListeners(){
        binding.imAttach.setOnClickListener {
            onPickPhoto()
        }

        binding.imAddPhoto.setOnClickListener {
            cameraPermission()
        }

        binding.imAddBillBookmark.setOnClickListener {
            if(bookmark) {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                bookmark = false
            } else {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                bookmark = true
            }
            //TODO Bookmark
        }

        binding.imAddBillBack.setOnClickListener {
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }
    }

    private fun editTextListeners(){
        binding.edDateAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(DATE)
            //Picker double calls. Because of setText calls Focus: clearFocus()
            if(checkFocus) {
                initDatePickerDialog()
                checkFocus = false
            }
            binding.edDateAdd.clearFocus()
            checkFocus = true
        }

        binding.edTimeAdd.setOnFocusChangeListener { view, b ->
            setColorStateEditText(TIME)
            //Picker double calls. Because of setText calls Focus: clearFocus()
            if(checkFocus) {
                initTimePicker()
                checkFocus = false
            }
            binding.edTimeAdd.clearFocus()
            checkFocus = true
        }

        binding.edAddCategory.setOnFocusChangeListener { view, b ->
            setColorStateEditText(CATEGORY)
            if(checkFocus) {
                var dialogCategory = FragmentDialogCategory()
                dialogCategory.show(requireActivity().supportFragmentManager, TAG_DIALOG_CATEGORY)
                dialogCategory.setFragmentResultListener(REQUESTKEY_CATEGORY_ITEM){ requestKey, bundle ->
                    // We use a String here, but any type that can be put in a Bundle is supported
                    binding.edAddCategory.setText(bundle.getString(BUNDLEKEY_CATEGORY_ITEM))
                    binding.edAddAmount.requestFocus()
                }
                checkFocus = false
            }
            binding.edAddCategory.clearFocus()
            checkFocus = true
        }

        binding.edAddAmount.setOnFocusChangeListener { view, b ->
            setColorStateEditText(AMOUNT)
        }

        binding.edAddNote.setOnFocusChangeListener { view, b ->
            setColorStateEditText(NOTE)
        }

        binding.edDescription.setOnFocusChangeListener { view, b ->
            setColorStateEditText(DESCRIPTION)
        }
    }

    private fun textViewListeners(){
        binding.tvAddExpenses.setOnClickListener {
            binding.tvAddExpenses.setBackgroundResource(R.drawable.textview_border_expense)
            binding.tvAddIncome.setBackgroundResource(0)
            TYPE_BILL = TYPE_EXPENSE
            colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_expense))
            binding.bAddSave.backgroundTintList = colorState
            isFocusEditText()
        }

        binding.tvAddIncome.setOnClickListener {
            binding.tvAddIncome.setBackgroundResource(R.drawable.textview_border_income)
            binding.tvAddExpenses.setBackgroundResource(0)
            TYPE_BILL = TYPE_INCOME
            colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_income))
            binding.bAddSave.backgroundTintList = colorState
            isFocusEditText()
        }
    }

    //If Focus change color too
    private fun isFocusEditText(){
        if(binding.edDateAdd.isFocused) binding.edDateAdd.backgroundTintList = colorState
        if(binding.edTimeAdd.isFocused) binding.edTimeAdd.backgroundTintList = colorState
        if(binding.edAddCategory.isFocused) binding.edAddCategory.backgroundTintList = colorState
        if(binding.edAddAmount.isFocused) binding.edAddAmount.backgroundTintList = colorState
        if(binding.edAddNote.isFocused) binding.edAddNote.backgroundTintList = colorState
        if(binding.edDescription.isFocused) binding.edDescription.backgroundTintList = colorState
        binding.edAddAmount.requestFocus() // Because When skip DialogView and color doesn't change
    }
    //Change Type color Expense - Income when you click on View
    private fun setColorStateEditText(editText : Int){
        var colorStateDefault = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_background))
        //First step set Default color
        binding.edDateAdd.backgroundTintList = colorStateDefault
        binding.edTimeAdd.backgroundTintList = colorStateDefault
        binding.edAddCategory.backgroundTintList = colorStateDefault
        binding.edAddAmount.backgroundTintList = colorStateDefault
        binding.edAddNote.backgroundTintList = colorStateDefault
        binding.edDescription.backgroundTintList = colorStateDefault
        when(editText){
            DATE -> binding.edDateAdd.backgroundTintList = colorState
            TIME -> binding.edTimeAdd.backgroundTintList = colorState
            CATEGORY -> binding.edAddCategory.backgroundTintList = colorState
            AMOUNT -> binding.edAddAmount.backgroundTintList = colorState
            NOTE -> binding.edAddNote.backgroundTintList = colorState
            DESCRIPTION -> binding.edDescription.backgroundTintList = colorState
        }
    }

    private fun initDatePickerDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireActivity(), { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            binding.edDateAdd.setText(SimpleDateFormat("dd.MM.yyyy").format(c.time))
            binding.edAddAmount.requestFocus()
        }, year, month, day)

        dpd.show()
    }

    private fun initTimePicker(){
        val c = Calendar.getInstance()
        val cHour = c.get(Calendar.HOUR_OF_DAY)
        val cMinute = c.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(requireContext(),
            { view, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                binding.edTimeAdd.setText(SimpleDateFormat("HH:mm a").format(c.time))
                binding.edAddAmount.requestFocus()
            }, cHour, cMinute, false)

        mTimePicker.show()
    }

    private fun initAutoCompleteEditText(list : MutableList<String>){
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, list)
        binding.edAddNote.setAdapter(adapter)
    }

    private fun cameraPermission(){

        when{
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED->{
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)-> getActivity()?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
            }

            else -> getActivity()?.let { ActivityCompat.requestPermissions(it, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS) }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create the File where the photo should go
        photoFile = createImageFile()

        // Continue only if the File was successfully created
        if(photoFile != null){
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.billsAplication.fileprovider", // Your package
                photoFile!!)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }

        if (requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // Start the image capture intent to take photo
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }

    }

    fun onPickPhoto() {
        // Create intent for picking a photo from the gallery
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(requireContext().getContentResolver(), photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            //Save image's bytes in array
            bitmapByte.add(ImageItem(
                loadFromUri(data?.data)!!.toByteArray(),
                ID_IMAGE))
            ID_IMAGE++
            imageAdapter.submitList(bitmapByte.toMutableList())
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Save image's bytes in array
            bitmapByte.add(ImageItem(photoFile!!.readBytes(), ID_IMAGE))
            ID_IMAGE++
            imageAdapter.submitList(bitmapByte.toMutableList())
            //delete File, cause we may do not save Image... It was like a buffer
            CoroutineScope(IO).launch {
                    deletePhoto(photoFile!!.absolutePath)
            }
        }
    }

    // extension function to convert bitmap to byte array
    fun Bitmap.toByteArray():ByteArray{
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,100,this)
            return toByteArray()
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun deletePhoto(strPath: String) {

        val path = Paths.get(strPath)

            try {
                val result = Files.deleteIfExists(path)
                if (result) {
                    println("Deletion succeeded.")
                } else {
                    println("Deletion failed.")
                }
            } catch (e: IOException) {
                println("Deletion failed.")
                e.printStackTrace()
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun onCreateDialog(item : ImageItem): Dialog? {
        val bMapScaled = Bitmap.createScaledBitmap(
            BitmapFactory.decodeByteArray(item.bytesImage,0, item.bytesImage.size),
            requireContext().display!!.width,
            requireContext().display!!.height,
            true)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity,
            android.R.style.Theme_Material_NoActionBar_TranslucentDecor)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.full_image_dialog, null)
        builder.setView(view)
        view.findViewById<ImageView>(R.id.im_fullScreen)
            .setImageBitmap(bMapScaled)
        return builder.create()
    }

}