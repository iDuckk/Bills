package com.billsAplication.presentation.fragmentDialogCategory

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.databinding.FragmentDialogCategoryBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.dialogCategory.DialogCategoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class FragmentDialogCategory : DialogFragment() {
    private var _binding: FragmentDialogCategoryBinding? = null
    private val binding : FragmentDialogCategoryBinding get() = _binding!!

    private val REQUESTKEY_CATEGORY_ITEM = "RequestKey_Category_item"
    private val BUNDLEKEY_CATEGORY_ITEM = "BundleKey_Category_item"

    private val KEY_CATEGORY_ITEM_SEND = "RequestKey_Category_item_SEND"

    private var typeCategory = 2

    @Inject
    lateinit var viewModel: FragmentDialogCategoryViewModel
    @Inject
    lateinit var dialogAdapter: DialogCategoryAdapter

    private val component by lazy {
        (requireActivity().application as BillsApplication).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogCategoryBinding.inflate(inflater, container, false)

        //get type of categories
        typeCategory = requireArguments().getInt(KEY_CATEGORY_ITEM_SEND)

        Log.d("TAG", typeCategory.toString())

        viewModel.getCategoryType(typeCategory)

        binding.tvDialogCategoryAdd.visibility = View.GONE

        viewModel.list.observe(viewLifecycleOwner){
            dialogAdapter.submitList(it.toMutableList())
        }

        initRecView()

        binding.imDialogClose.setOnClickListener {
            dismiss()
        }
        //TODO SEARCH Frag

        //Listener for changing of editText
        listenerForChangingTV()

        listenerAddCategory()

        listenerForEnterKey()

        return binding.root
    }

    private fun listenerForEnterKey() {
        binding.edDialogCategoryAdd.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val text = binding.edDialogCategoryAdd.text.toString()

                if (text.isNotEmpty()) {
                    CoroutineScope(IO).launch {
                        viewModel.addCategory(
                            BillsItem(
                                0,
                                typeCategory,
                                "",
                                "",
                                "",
                                text,
                                "",
                                "",
                                "",
                                false, "", "", "", "", ""
                            )
                        )
                    }
                    v.hideKeyboard()
                    v.text = ""
                    v.clearFocus()
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun listenerAddCategory() {
        binding.tvDialogCategoryAdd.setOnClickListener {
            val text = binding.edDialogCategoryAdd.text.toString()
                if (dialogAdapter.currentList.find { text == it.category }?.category != text) {
                    CoroutineScope(IO).launch {
                        viewModel.addCategory(
                            BillsItem(
                                0,
                                typeCategory,
                                "",
                                "",
                                "",
                                text,
                                "",
                                "",
                                "",
                                false, "", "", "", "", ""
                            )
                        )
                    }
                    binding.edDialogCategoryAdd.hideKeyboard()
                    binding.edDialogCategoryAdd.setText("")
                    binding.edDialogCategoryAdd.clearFocus()
                }
        }
    }

    private fun listenerForChangingTV() {
        binding.edDialogCategoryAdd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Hide Add bottom
                if (p0?.length != 0)
                    binding.tvDialogCategoryAdd.visibility = View.VISIBLE
                else
                    binding.tvDialogCategoryAdd.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initRecView(){
        with(binding.recViewDialogCategory){
            layoutManager = GridLayoutManager(context, 2)
            adapter = dialogAdapter

        }
        dialogAdapter.onClickListenerDelete = {
            CoroutineScope(IO).launch {
                viewModel.deleteCategory(it)
            }
        }

        dialogAdapter.onClickListenerGetItem = {
            setFragmentResult(REQUESTKEY_CATEGORY_ITEM, bundleOf(BUNDLEKEY_CATEGORY_ITEM to it.category))
            dismiss()
        }
    }

}