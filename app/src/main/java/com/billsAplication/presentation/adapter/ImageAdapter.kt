package com.billsAplication.presentation.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.ImageItemBinding
import com.billsAplication.domain.model.ImageItem
import javax.inject.Inject

var onClickListenerDeleteImage: ((item : ImageItem) -> Unit)? = null
var onClickListenerSaveImage: ((item : ImageItem) -> Unit)? = null
var onClickListenerItem: ((item : ImageItem) -> Unit)? = null

class ImageAdapter @Inject constructor(): ListAdapter<ImageItem, ImageViewHolder>(ImageCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)

        holder.im_delete.setOnClickListener {
            onClickListenerDeleteImage?.invoke(item)
        }

        holder.im_save.setOnClickListener {
            onClickListenerSaveImage?.invoke(item)
        }

        holder.im_preview.setOnClickListener {
            onClickListenerItem?.invoke(item)
        }

        holder.im_preview.setImageBitmap(BitmapFactory.decodeByteArray(item.bytesImage,0, item.bytesImage.size))
    }

}