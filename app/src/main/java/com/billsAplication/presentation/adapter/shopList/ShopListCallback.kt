package com.billsAplication.presentation.adapter.shopList

import androidx.recyclerview.widget.DiffUtil
import com.billsAplication.domain.model.ShopListItem

class ShopListCallback: DiffUtil.ItemCallback<ShopListItem>() {
    override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
        return oldItem == newItem
    }
}