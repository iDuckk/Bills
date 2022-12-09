
package com.billsAplication.presentation.addBill

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentAddBillBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.ImageItem
import com.billsAplication.presentation.adapter.image.ImageAdapter
import com.billsAplication.presentation.adapter.image.onClickListenerDeleteImage
import com.billsAplication.presentation.adapter.image.onClickListenerItem
import com.billsAplication.presentation.adapter.image.onClickListenerSaveImage
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategory
import com.billsAplication.utils.*
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.math.log


@SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
class AddBillFragment : Fragment() {

    private var _binding: FragmentAddBillBinding? = null
    private val binding : FragmentAddBillBinding get() = _binding!!

    private val REQUEST_WRITE_EX_STORAGE_PERMISSION = 122
    private val REQUEST_CODE_PERMISSIONS = 100
    private val REQUEST_IMAGE_CAPTURE = 102
    private val PICK_IMAGE = 109
    private val ADD_BILL_KEY = "add_bill_key"
    private val BILL_ITEM_KEY = "bill_item_key"
    private val REQUESTKEY_CATEGORY_ITEM = "RequestKey_Category_item"
    private val BUNDLEKEY_CATEGORY_ITEM = "BundleKey_Category_item"
    private val KEY_CATEGORY_ITEM_SEND = "RequestKey_Category_item_SEND"
    private val TAG_DIALOG_CATEGORY = "Dialog Category"
    private val TYPE_EXPENSE = 0
    private val TYPE_INCOME = 1
    private val TYPE_NOTE = 3
    private val TYPE_CATEGORY_EXPENSES = 2
    private val TYPE_CATEGORY_INCOME = 4
    private val DATE = 0
    private val TIME = 1
    private val CATEGORY = 2
    private val AMOUNT = 3
    private val NOTE = 4
    private val DESCRIPTION = 5
    private val MAX_PHOTO = 5
    private val EMPTY_STRING = ""
    private val widthPhoto = 600f
    private val heightPhoto = 800f
    private val providerPackageApp = "com.billsAplication.fileprovider"

    private val imageList: MutableList<ImageItem> = ArrayList()

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
    private val UPDATE_TYPE_SEARCH = 103
    private var TYPE_ENTRENCE = 0
    private var TIME_FORMAT_24 = ""
    private var scope = CoroutineScope(Dispatchers.Main)


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
    @Inject
    lateinit var fadeOutView: FadeOutView
    @Inject
    lateinit var fadeInView: FadeInView
    @Inject
    lateinit var motionViewX: MotionViewX

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
        (context as InterfaceMainActivity).navBottom().visibility = View.GONE
        //Set recView invisible
        binding.recViewPhoto.visibility = View.GONE

        //Set Currency of amount EditText - Default currency
        binding.tvCurrancy.text = CurrentCurrency.currency

        initAutoCompleteEditText()

        imageListeners()

        initRecViewImage()

        textViewListeners()

        editTextListeners()

        //BillItem when we update item
        billItem = arguments?.getParcelable(BILL_ITEM_KEY)
        TYPE_ENTRENCE = requireArguments().getInt(ADD_BILL_KEY)
        when(TYPE_ENTRENCE){
            TYPE_ADD -> setViewsCreateType()
            TYPE_UPDATE -> setViewsEditType()
            UPDATE_TYPE_SEARCH -> setViewsEditType()
            TYPE_BOOKMARK -> setViewsBookmarksType()
            else -> mToast(getString(R.string.Wrong_entrence_type))
        }
        //Because TransactionTooLargeException
        arguments?.clear()

    }

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
            dialogDeleteImage(it)
        }

        onClickListenerSaveImage = {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                storagePermission(it)
            }else{
                dialogSaveImage(it)
            }
        }

        onClickListenerItem = {
            onCreateDialog(it)!!.show()
        }
    }

    private fun deletePhoto(it: ImageItem) {
        imageList.remove(it)
        imageAdapter.submitList(imageList.toMutableList())
        countPhoto--
        //Check how many photo we added
        checkAmountPhoto()
        //Set RecView invisible
        if (countPhoto == 0)
            binding.recViewPhoto.visibility = View.GONE
        //if type is Edit
        binding.bAddSave.isEnabled = true
    }

    private fun imageListeners(){
        binding.imAttach.setOnClickListener {
            onPickPhoto()
        }

        binding.imAddPhoto.setOnClickListener {
            cameraPermission()
        }

        binding.imAddBillBookmark.setOnClickListener {
            //if type is Edit
            if(TYPE_ENTRENCE == TYPE_UPDATE){
                if(bookmark) {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                    bookmark = false
                    mToast(getString(R.string.unsaved_bookmark))
                } else {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                    bookmark = true
                    mToast(getString(R.string.saved_bookmark))
                }
                if (!binding.edAddAmount.text.isNullOrEmpty() && !binding.edAddCategory.text.isNullOrEmpty()) {
                    CoroutineScope(IO).launch {
                        viewModel.update(createBillItem())
                    }
                    findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
                } else doNotFillAllGapsAttention()
            } else if(TYPE_ENTRENCE == UPDATE_TYPE_SEARCH){
                if(bookmark) {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                    bookmark = false
                    mToast(getString(R.string.unsaved_bookmark))
                } else {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                    bookmark = true
                    mToast(getString(R.string.saved_bookmark))
                }
                if (!binding.edAddAmount.text.isNullOrEmpty() && !binding.edAddCategory.text.isNullOrEmpty()) {
                    CoroutineScope(IO).launch {
                        viewModel.update(createBillItem())
                    }
                    findNavController().navigate(R.id.action_addBillFragment_to_searchFragment)
                } else doNotFillAllGapsAttention()
            } else{
                if(bookmark) {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
                    bookmark = false
                } else {
                    binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
                    bookmark = true
                }
            }
        }

        binding.imAddBillBack.setOnClickListener {
            requireActivity().onBackPressed()
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
                val dialogCategory = FragmentDialogCategory()

                val args = Bundle()
                //sent type Category to Dialog
                if(TYPE_BILL == TYPE_EXPENSE)
                    args.putInt(KEY_CATEGORY_ITEM_SEND, TYPE_CATEGORY_EXPENSES)
                else
                    args.putInt(KEY_CATEGORY_ITEM_SEND, TYPE_CATEGORY_INCOME)
                dialogCategory.arguments = args

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
    private fun setViewsCreateType(){
        //Set Date
        binding.edDateAdd.setText(SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time))
        //Set Time
        binding.edTimeAdd.setText(SimpleDateFormat("hh:mm a").format(Calendar.getInstance().time))
        //Save time format 24 for DB
        TIME_FORMAT_24 = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        //Add new Item
        clickListenerButtonAddSave()
    }

    //Set views when Edit type
    private fun setViewsEditType(){
        if(billItem != null){
            setTypeBill()
            setBookmarkImage()
            //set EditTexts
            binding.edDateAdd.setText(billItem?.date.toString())
            binding.edTimeAdd.setText(timeFormat(billItem?.time.toString()))
            //Save time format 24 for DB
            TIME_FORMAT_24 = billItem?.time.toString()
            binding.edAddCategory.setText(billItem?.category.toString())
            binding.edAddAmount.setText(billItem?.amount.toString())
            binding.edAddNote.setText(billItem?.note.toString())
            binding.edDescription.setText(billItem?.description.toString())
            //set ImageViews
            addImageItemToList()

            binding.bAddSave.isEnabled = false
        }
        //if Views' content is changed
        changeViewsListeners()
        clickListenerButtonEditSave()
    }

    private fun setViewsBookmarksType() {
        if(billItem != null) {
            //set type bill
            setTypeBill()
            //Set Date
            binding.edDateAdd.setText(SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time))
            //Set Time
            binding.edTimeAdd.setText(SimpleDateFormat("hh:mm a").format(Calendar.getInstance().time))
            //Save time format 24 for DB
            TIME_FORMAT_24 = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
            //set EditTexts others EditText
            binding.edAddCategory.setText(billItem?.category.toString())
            binding.edAddAmount.setText(billItem?.amount.toString())
            binding.edAddNote.setText(billItem?.note.toString())
            binding.edDescription.setText(billItem?.description.toString())
            //set ImageViews
            addImageItemToList()
            clickListenerButtonAddSave()
        }
    }

    private fun timeFormat(time: String):String{
        var hour = "${time.get(0)}${time.get(1)}".toInt()
        var minute = "${time.get(3)}${time.get(4)}".toInt()

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)

        return SimpleDateFormat("hh:mm a").format(c.time)
    }

    private fun clickListenerButtonAddSave() {
        binding.bAddSave.setOnClickListener {
            if (!binding.edAddAmount.text.isNullOrEmpty() && !binding.edAddCategory.text.isNullOrEmpty()) {
                //Add new BillItem
                CoroutineScope(IO).launch {
                    viewModel.add(createBillItem())
                }
                findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
            } else {
                doNotFillAllGapsAttention()
            }
        }
    }

    private fun clickListenerButtonEditSave(){
        binding.bAddSave.setOnClickListener {
            if (!binding.edAddAmount.text.isNullOrEmpty() && !binding.edAddCategory.text.isNullOrEmpty()) {
                CoroutineScope(IO).launch {
                    viewModel.update(createBillItem())
                }
                if(TYPE_ENTRENCE == UPDATE_TYPE_SEARCH)
                    findNavController().navigate(R.id.action_addBillFragment_to_searchFragment)
                else
                    findNavController().navigate(R.id.action_addBillFragment_to_billsListFragment)
            } else doNotFillAllGapsAttention()
        }
    }

    private fun doNotFillAllGapsAttention() {
        when {
            binding.edAddAmount.text.isNullOrEmpty() -> mToast(getString(R.string.toast_fill_amount))
            binding.edAddCategory.text.isNullOrEmpty() -> mToast(getString(R.string.toast_fill_category))
            else -> mToast(getString(R.string.toast_fill_both_gaps))
        }
        binding.edAddAmount.requestFocus()
    }

    private fun addImageItemToList() {
        if (!billItem?.image1.isNullOrEmpty()) imageList.add(
            ImageItem(
                billItem!!.image1,
                ID_IMAGE++
            )
        )
        if (!billItem?.image2.isNullOrEmpty()) imageList.add(
            ImageItem(
                billItem!!.image2,
                ID_IMAGE++
            )
        )
        if (!billItem?.image3.isNullOrEmpty()) imageList.add(
            ImageItem(
                billItem!!.image3,
                ID_IMAGE++
            )
        )
        if (!billItem?.image4.isNullOrEmpty()) imageList.add(
            ImageItem(
                billItem!!.image4,
                ID_IMAGE++
            )
        )
        if (!billItem?.image5.isNullOrEmpty()) imageList.add(
            ImageItem(
                billItem!!.image5,
                ID_IMAGE++
            )
        )
        if (imageList.isNotEmpty()) {
            imageAdapter.submitList(imageList.toMutableList())
            binding.recViewPhoto.visibility = View.VISIBLE

            checkAmountPhoto()
        }
    }

    private fun setBookmarkImage() {
        if (billItem!!.bookmark) {
            binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_enable)
            bookmark = true
        } else {
            binding.imAddBillBookmark.setImageResource(R.drawable.ic_bookmark_disable)
            bookmark = false
        }
    }

    private fun setTypeBill() {
        if (billItem?.type == TYPE_INCOME) {
            setTypeIncome()
        } else if (billItem?.type == TYPE_EXPENSE) {
            setTypeExpense()
        } else mToast(getString(R.string.Error_incorrect_typeOfBill))
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
            if(TYPE_ENTRENCE == TYPE_ADD || TYPE_ENTRENCE == TYPE_BOOKMARK) 0 else billItem!!.id,
            TYPE_BILL,
            date.month.toString() + " " + date.year.toString(),
            binding.edDateAdd.text.toString(),
            TIME_FORMAT_24,
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
        val dpd = DatePickerDialog(requireActivity(),
            if(TYPE_BILL == TYPE_INCOME)R.style.DialogTheme_income else R.style.DialogTheme_expense,
            { view, year, monthOfYear, dayOfMonth ->
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
            if(TYPE_BILL == TYPE_INCOME)R.style.DialogTheme_income else R.style.DialogTheme_expense,
            { view, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                //Save time string that save time in 24 format
                TIME_FORMAT_24 = SimpleDateFormat("HH:mm").format(c.time)

                binding.edTimeAdd.setText(SimpleDateFormat("hh:mm a").format(c.time))
                binding.edAddAmount.requestFocus()
            }, cHour, cMinute, false)

        mTimePicker.show()
    }

    private fun initAutoCompleteEditText(){
            val list: MutableList<String> = ArrayList()
            //Create list of Notes
            viewModel.getAll()
            viewModel.list.observe(viewLifecycleOwner) { item ->
                item.forEach {
                    if(it.note != EMPTY_STRING && it.type != TYPE_NOTE)
                    list.add(it.note)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    list.distinct()
                )
                binding.edAddNote.setAdapter(adapter)
            }
    }

    private fun storagePermission(it: ImageItem){
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            checkStoragePermission()
        }else{
            dialogSaveImage(it)
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EX_STORAGE_PERMISSION
            )
        }
    }

    @SuppressLint("InlinedApi")
    private fun saveImage(it: ImageItem){
        val resolver = requireActivity().contentResolver
        val c = Calendar.getInstance()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(c.time)
        //Decode String to Bytes
        val byteImage = Base64.getDecoder().decode(it.stringImage)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "JPEG_${timeStamp}_${c.timeInMillis}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BillsApplication")
            }
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use {
                it?.write(byteImage)
                it?.flush()
                it?.close()
            }
        }
//        Save image on storage
//        MediaStore.Images.Media.insertImage(
//            resolver,
//            BitmapFactory.decodeByteArray(byteImage,0, byteImage.size), //To BitMap
//            SimpleDateFormat("yyyyMMdd").format(Date()) + "_${it.id}",
//            null
//        )
    }

    private fun dialogSaveImage(it: ImageItem){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder
            .setTitle(getString(R.string.dialog_title_save_image))
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.dialog_message_save_dialog))
            .setPositiveButton(getString(R.string.b_save_note)){
                    dialog, id ->
                saveImage(it)
            }
            .setNegativeButton(getString(R.string.search_cancel), null)
            .create()
        dialog.show()
    }

    private fun dialogDeleteImage(it: ImageItem){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val dialog =  builder
            .setTitle(getString(R.string.dialog_title_delete_image))
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.dialog_message_delete_dialog))
            .setPositiveButton(getString(R.string.b_delete_note)){
                    dialog, id ->
                deletePhoto(it)
            }
            .setNegativeButton(getString(R.string.search_cancel), null)
            .create()
        dialog.show()
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
                providerPackageApp, // Your package
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val loadIm = loadImage(data?.data)!!.toByteArray()
            //Save image as a Bitmap
            val image = BitmapFactory.decodeByteArray(loadIm,0, loadIm.size)
            //New scale of image
            val matrix = Matrix()
            matrix.postScale(widthPhoto/image.width, heightPhoto/image.height)
            val bitmap = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true )
            //Decode String to Bytes than Save image's bytes in array
            imageList.add(ImageItem(Base64.getEncoder().encodeToString(bitmap.toByteArray()), ID_IMAGE))
//            imageList.add(ImageItem(
//                Base64.getEncoder().encodeToString(loadImage(data?.data)!!.toByteArray()),
//                ID_IMAGE))
            ID_IMAGE++ // Amount of images
            //Set RecView visible
            binding.recViewPhoto.visibility = View.VISIBLE
            imageAdapter.submitList(imageList.toMutableList())
            //if type is Edit
            binding.bAddSave.isEnabled = true
            binding.bAddSave.isEnabled = true
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Decode String to Bytes than Save image's bytes in array
//            imageList.add(ImageItem(Base64.getEncoder().encodeToString(photoFile!!.readBytes()), ID_IMAGE))
//            ID_IMAGE++ // Amount of images
            //Save image as a Bitmap
//            val image = BitmapFactory.decodeByteArray(photoFile!!.readBytes(),0, photoFile!!.readBytes().size)
            //Use loadImage class instead just "photoFile!!.readBytes()", cause in Samsung photo rotated...
            val loadIm = loadImage(photoFile!!.toUri())!!.toByteArray()
            val image = BitmapFactory.decodeByteArray(loadIm,0, loadIm.size)
            //New scale of image
            val matrix = Matrix()
            matrix.postScale(widthPhoto/image.width, heightPhoto/image.height)
            val bitmap = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true )
//            val bitmap = Bitmap.createScaledBitmap(image, 480, 640, false)
            //Save image as a String
            imageList.add(ImageItem(Base64.getEncoder().encodeToString(bitmap.toByteArray()), ID_IMAGE))
            ID_IMAGE++ // Amount of images
            //Set RecView visible
            binding.recViewPhoto.visibility = View.VISIBLE
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
        checkAmountPhoto()
    }

    private fun checkAmountPhoto() {
        countPhoto = imageList.size
        if (countPhoto == MAX_PHOTO) {
            binding.imAddPhoto.isEnabled = false
            binding.imAttach.isEnabled = false
        } else {
            binding.imAddPhoto.isEnabled = true
            binding.imAttach.isEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun onCreateDialog(item : ImageItem): Dialog? {
        //Get bytes
        val byteImage = Base64.getDecoder().decode(item.stringImage)
        //Create View of full Image layout
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.full_image_dialog, null)
        //Create imageView
        val imageView = view.findViewById<PhotoView>(R.id.im_fullScreen)
        //Create dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity,
            R.style.full_screen_dialog)
        //Set layout
        builder.setView(view)
        //set Image
        Glide
            .with(requireContext())
            .load(byteImage)
            .override(requireContext().display!!.width, requireContext().display!!.height)
            .fitCenter()
            .into(imageView)

        return builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.addFlags(FLAG_FULLSCREEN)
        }
    }

    //Set expense type after join to fragment
    private fun setTypeExpense(){
        TYPE_BILL = TYPE_EXPENSE
        colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_expense))
        binding.tvAddBillSearch.setText(requireContext().getString(R.string.bill_list_expense))
        chooseTpe()
    }
    //set income type after join to fragment
    private fun setTypeIncome(){
        TYPE_BILL = TYPE_INCOME
        colorState = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_income))
        binding.tvAddBillSearch.setText(requireContext().getString(R.string.bills_list_income))
        chooseTpe()
    }

    private fun chooseTpe(){
        binding.bAddSave.backgroundTintList = colorState
        binding.imAddBillBookmark.visibility = View.VISIBLE
        isFocusEditText()
        if(TYPE_ENTRENCE == TYPE_ADD) {
            scope.launch {
                motionViewX(binding.tvAddExpenses, 0f, binding.tvAddExpenses.width.toFloat())
                motionViewX(binding.tvAddIncome, 0f, -binding.tvAddExpenses.width.toFloat())
                delay(300)
                binding.cardViewTypeBill.visibility = View.GONE
                binding.cardViewAddThirdPart.visibility = View.VISIBLE
            }
        }else{
            binding.cardViewTypeBill.visibility = View.GONE
            binding.cardViewAddThirdPart.visibility = View.VISIBLE
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

    // extension function to convert bitmap to byte array
    fun Bitmap.toByteArray():ByteArray{
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,100,this)
            return toByteArray()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        _binding = null
    }

}