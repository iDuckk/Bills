package com.billsAplication.presentation.adapter.image

import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.databinding.ImageItemBinding

class ImageViewHolder( binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val im_preview = binding.imPhotoPreview
    val im_delete = binding.imDelete
    val im_save = binding.imSave

}