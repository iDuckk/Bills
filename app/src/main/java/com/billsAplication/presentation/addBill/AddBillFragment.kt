
package com.billsAplication.presentation.addBill

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddBillBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.ImageItem
import com.billsAplication.presentation.adapter.*
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategory
import com.billsAplication.presentation.mainActivity.MainActivity
import com.billsAplication.utils.CreateImageFile
import com.billsAplication.utils.DeleteFIle
import com.billsAplication.utils.LoadImageFromGallery
import com.billsAplication.utils.mToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.*
import javax.inject.Inject


@SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
class AddBillFragment : Fragment() {

    private var _binding: FragmentAddBillBinding? = null
    private val binding : FragmentAddBillBinding get() = _binding!!

    private val REQUEST_CODE_PERMISSIONS = 100
    private val REQUEST_IMAGE_CAPTURE = 102
    private val PICK_IMAGE = 109
    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
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
    private val MAX_PHOTO = 5

    private val imageList: MutableList<ImageItem> = ArrayList()

    var checkNoteObserve = true
    private var ID_IMAGE = 0
    private var photoFile: File? = null
    private var TYPE_BILL = TYPE_EXPENSE
    private var bookmark = false
    private var checkFocus = true
    private var countPhoto = 0
    private var billItem: BillsItem? = null
    private var TYPE_UPDATE = 101
    private var TYPE_ADD = 100
    private var TYPE_BOOKMARK = 102

    lateinit var colorState : ColorStateList
    @Inject
    lateinit var loadImage: LoadImageFromGallery
    @Inject
    lateinit var createImageFile: CreateImageFile
    @Inject
    lateinit var deleteFile: DeleteFIle
    @Inject
    lateinit var mToast: mToast
    @Inject
    lateinit var viewModel: AddBillViewModel
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
        binding.tvCurrancy.text = DecimalFormat().currency!!.currencyCode

        initAutoCompleteEditText()

        imageListeners()

        initRecViewImage()

        textViewListeners()

        editTextListeners()

        //BillItem when we update item
        billItem = arguments?.getParcelable(BILL_ITEM_KEY)
        when(requireArguments().getInt(ADD_BILL_KEY)){
            TYPE_ADD -> setViewsCreateType()
            TYPE_UPDATE -> setViewsEditType()
            TYPE_BOOKMARK -> mToast("Bookmark")
            else -> mToast("AddBillFragment: Wrong type of entrance")
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initRecViewImage(){

        imageAdapter.submitList(imageList.toMutableList())

        with(binding.recViewPhoto){
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter

        }
        onClickListenerDeleteImage = {
            imageList.remove(it)
            imageAdapter.submitList(imageList.toMutableList())
            //Check how many photo we added
            if(countPhoto == MAX_PHOTO){
                binding.imAddPhoto.isEnabled = true
                binding.imAttach.isEnabled = true
            }
            countPhoto--
            //if type is Edit
            binding.bAddSave.isEnabled = true
        }

        onClickListenerSaveImage = {
            //Decode String to Bytes
            val byteImage = Base64.getDecoder().decode(it.stringImage)
            //Save image on storage
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                BitmapFactory.decodeByteArray(byteImage,0, byteImage.size), //To BitMap
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
                mToast(getString(R.string.unsaved_bookmark))
            } else {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                bookmark = true
                mToast(getString(R.string.saved_bookmark))
            }
            //if type is Edit
            binding.bAddSave.isEnabled = true
        }

        binding.imAddBillBack.setOnClickListener {
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            setTypeExpense()
        }

        binding.tvAddIncome.setOnClickListener {
            setTypeIncome()
        }
    }
    //set views when create type
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setViewsCreateType(){
        //Set Date
        binding.edDateAdd.setText(SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time))
        //Set Time
        binding.edTimeAdd.setText(SimpleDateFormat("HH:mm a").format(Calendar.getInstance().time))
        //Set Expense TextView as default
        setTypeExpense()
        //Add new Item
        binding.bAddSave.setOnClickListener {
            if(!binding.edAddAmount.text.isNullOrEmpty() && !binding.edAddCategory.text.isNullOrEmpty()) {
                //Add new BillItem
                CoroutineScope(IO).launch {
                    viewModel.add(createBillItem())
                }
                //Because TransactionTooLargeException
                arguments?.clear()
                findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
            }else {
                if(binding.edAddAmount.text.isNullOrEmpty())
                    mToast(getString(R.string.toast_fill_amount))
                else if(binding.edAddCategory.text.isNullOrEmpty())
                    mToast(getString(R.string.toast_fill_category))
                else mToast(getString(R.string.toast_fill_both_gaps))
                binding.edAddAmount.requestFocus()
            }
        }
    }
    //Set views when Edit type
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setViewsEditType(){
        if(billItem != null){
            //set type bill
            if(billItem?.type == TYPE_INCOME){
                setTypeIncome()
            }else if(billItem?.type == TYPE_EXPENSE){
                setTypeExpense()
            }else mToast(getString(R.string.Error_incorrect_typeOfBill))
            //set Bookmark
            if(billItem!!.bookmark) {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                bookmark = true
            }
            else {
                binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                bookmark = false
            }
            //set EditTexts
            binding.edDateAdd.setText(billItem?.date.toString())
            binding.edTimeAdd.setText(billItem?.time.toString())
            binding.edAddCategory.setText(billItem?.category.toString())
            binding.edAddAmount.setText(billItem?.amount.toString())
            binding.edAddNote.setText(billItem?.note.toString())
            binding.edDescription.setText(billItem?.description.toString())
            //set ImageViews
            if(!billItem?.image1.isNullOrEmpty()) imageList.add(ImageItem(billItem!!.image1, ID_IMAGE++))
            if(!billItem?.image2.isNullOrEmpty()) imageList.add(ImageItem(billItem!!.image2, ID_IMAGE++))
            if(!billItem?.image3.isNullOrEmpty()) imageList.add(ImageItem(billItem!!.image3, ID_IMAGE++))
            if(!billItem?.image4.isNullOrEmpty()) imageList.add(ImageItem(billItem!!.image4, ID_IMAGE++))
            if(!billItem?.image5.isNullOrEmpty()) imageList.add(ImageItem(billItem!!.image5, ID_IMAGE++))
            if(!imageList.isNullOrEmpty())
                imageAdapter.submitList(imageList.toMutableList())

            binding.bAddSave.isEnabled = false
        }
        //if Views' content is changed
        changeViewsListeners()

        binding.bAddSave.setOnClickListener{
            CoroutineScope(IO).launch {
                viewModel.update(createBillItem())
            }
            //Because TransactionTooLargeException
            arguments?.clear()
            findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
        }
    }

    private fun changeViewsListeners(){
        binding.edDateAdd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })
        binding.edTimeAdd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })
        binding.edAddCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })
        binding.edAddAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })
        binding.edAddNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })
        binding.edDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                binding.bAddSave.isEnabled = true
            } })

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBillItem(): BillsItem{
        var image1 = ""
        var image2 = ""
        var image3 = ""
        var image4 = ""
        var image5 = ""

        val day = binding.edDateAdd.text.dropLast(8).toString()
        val month = binding.edDateAdd.text.drop(3).dropLast(5).toString()
        val year = binding.edDateAdd.text.drop(6).toString()
        val date = LocalDate.of(year.toInt(),month.toInt(), day.toInt())

        imageList.forEachIndexed { index, imageItem ->
            when(index){
                0 -> image1 = imageItem.stringImage
                1 -> image2 = imageItem.stringImage
                2 -> image3 = imageItem.stringImage
                3 -> image4 = imageItem.stringImage
                4 -> image5 = imageItem.stringImage
            }
        }

        return BillsItem(
            if(billItem == null) 0 else billItem!!.id,
            TYPE_BILL,
            date.month.toString() + " " + date.year.toString(),
            binding.edDateAdd.text.toString(),
            binding.edTimeAdd.text.toString(),
            binding.edAddCategory.text.toString(),
            binding.edAddAmount.text.toString(),
            binding.edAddNote.text.toString(),
            binding.edDescription.text.toString(),
            bookmark,
            image1,
            image2,
            image3,
            image4,
            image5
        )
    }

    private fun initDatePickerDialog(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireActivity(), { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            binding.edDateAdd.setText(SimpleDateFormat("dd/MM/yyyy").format(c.time))
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

    private fun initAutoCompleteEditText(){
            val list: MutableList<String> = ArrayList()
            //Create list of Notes
            viewModel.getAll()
            viewModel.list.observe(requireActivity()) { item ->
                item.forEach {
                    list.add(it.note)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    list.distinct()
                )
                if(checkNoteObserve) { //Throw Null Exception when we leave Fragments
                    binding.edAddNote.setAdapter(adapter)
                    checkNoteObserve = false
                }

                viewModel.list.removeObservers(this)
            }
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
        photoFile = createImageFile.invoke()

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

    private fun onPickPhoto() {
        // Create intent for picking a photo from the gallery
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_IMAGE)
//        }
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            //Decode String to Bytes than Save image's bytes in array
            imageList.add(ImageItem(
                Base64.getEncoder().encodeToString(loadImage(data?.data)!!.toByteArray()),
                ID_IMAGE))
            ID_IMAGE++
            imageAdapter.submitList(imageList.toMutableList())
            //if type is Edit
            binding.bAddSave.isEnabled = true
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Decode String to Bytes than Save image's bytes in array
            imageList.add(ImageItem(Base64.getEncoder().encodeToString(photoFile!!.readBytes()), ID_IMAGE))
            ID_IMAGE++
            imageAdapter.submitList(imageList.toMutableList())
            //delete File, cause we may do not save Image... It was like a buffer
            CoroutineScope(IO).launch {
                deleteFile(photoFile!!.absolutePath)
            }
            //if type is Edit
            binding.bAddSave.isEnabled = true
        }
        //Check how many photo we added
        countPhoto++
        if(countPhoto == MAX_PHOTO){
            binding.imAddPhoto.isEnabled = false
            binding.imAttach.isEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun onCreateDialog(item : ImageItem): Dialog? {
        val byteImage = Base64.getDecoder().decode(item.stringImage)
        val bMapScaled = Bitmap.createScaledBitmap(
            BitmapFactory.decodeByteArray(byteImage,0, byteImage.size),
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
    //Set expense type after join to fragment
    private fun setTypeExpense(){
        binding.tvAddExpenses.setBackgroundResource(R.drawable.textview_border_expense)
        binding.tvAddIncome.setBackgroundResource(0)
        TYPE_BILL = TYPE_EXPENSE
        colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_expense))
        binding.bAddSave.backgroundTintList = colorState
        binding.tvAddExpenses.isEnabled = false
        binding.tvAddIncome.isEnabled = true
        //if type is Edit
        binding.bAddSave.isEnabled = true
        isFocusEditText()
    }
    //set income type after join to fragment
    private fun setTypeIncome(){
        binding.tvAddIncome.setBackgroundResource(R.drawable.textview_border_income)
        binding.tvAddExpenses.setBackgroundResource(0)
        TYPE_BILL = TYPE_INCOME
        colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_income))
        binding.bAddSave.backgroundTintList = colorState
        binding.tvAddExpenses.isEnabled = true
        binding.tvAddIncome.isEnabled = false
        //if type is Edit
        binding.bAddSave.isEnabled = true
        isFocusEditText()
    }


    // extension function to convert bitmap to byte array
    fun Bitmap.toByteArray():ByteArray{
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,100,this)
            return toByteArray()
        }
    }

}