package com.billsAplication.presentation.adapter.search_analytics

import androidx.recyclerview.widget.DiffUtil
import com.billsAplication.domain.model.BillsItem

class BillsListCallback_SA: DiffUtil.ItemCallback<BillsItem>() {
    override fun areItemsTheSame(oldItem: BillsItem, newItem: BillsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BillsItem, newItem: BillsItem): Boolean {
        return oldItem == newItem
    }
}