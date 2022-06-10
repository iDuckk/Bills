package com.billsAplication.presentation.adapter.dialogCategory

import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.databinding.CategoryItemBinding

class DialogCategoryViewHolder( binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val tv_Category = binding.tvItemCategory
    val im_Delete = binding.imItemDelete

}