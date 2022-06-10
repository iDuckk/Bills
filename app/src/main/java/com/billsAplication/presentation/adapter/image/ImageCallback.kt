package com.billsAplication.presentation.adapter.image

import androidx.recyclerview.widget.DiffUtil
import com.billsAplication.domain.model.ImageItem

class ImageCallback: DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}