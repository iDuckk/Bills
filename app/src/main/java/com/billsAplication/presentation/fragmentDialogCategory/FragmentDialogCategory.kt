package com.billsAplication.presentation.fragmentDialogCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import com.billsAplication.BillsApplication
import com.billsAplication.databinding.FragmentDialogCategoryBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.DialogCategoryAdapter
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
                binding.edDialogCategoryAdd.setText("")
                binding.edDialogCategoryAdd.clearFocus()
            }
        }

        viewModel.getCategoryType()

        viewModel.list.observe(requireActivity()){
            dialogAdapter.submitList(it.toMutableList())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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