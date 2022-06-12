package com.billsAplication.presentation.fragmentDialogCategory

import android.content.Context
import android.os.Bundle
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

        initRecView()

        binding.imDialogClose.setOnClickListener {
            dismiss()
        }

        binding.tvDialogCategoryAdd.setOnClickListener {
            val text = binding.edDialogCategoryAdd.text.toString()

            if(text.isNotEmpty()) {
                CoroutineScope(IO).launch {
                    viewModel.addCategory(
                        BillsItem(
                            0,
                            2,
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

        binding.edDialogCategoryAdd.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val text = binding.edDialogCategoryAdd.text.toString()

                if(text.isNotEmpty()) {
                    CoroutineScope(IO).launch {
                        viewModel.addCategory(
                            BillsItem(
                                0,
                                2,
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

        viewModel.getCategoryType()

        viewModel.list.observe(viewLifecycleOwner){
            dialogAdapter.submitList(it.toMutableList())
        }

        return binding.root
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