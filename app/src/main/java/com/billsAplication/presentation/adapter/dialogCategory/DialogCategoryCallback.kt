package com.billsAplication.presentation.adapter.dialogCategory

import androidx.recyclerview.widget.DiffUtil
import com.billsAplication.domain.model.BillsItem

class DialogCategoryCallback: DiffUtil.ItemCallback<BillsItem>() {
    override fun areItemsTheSame(oldItem: BillsItem, newItem: BillsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BillsItem, newItem: BillsItem): Boolean {
        return oldItem == newItem
    }
}