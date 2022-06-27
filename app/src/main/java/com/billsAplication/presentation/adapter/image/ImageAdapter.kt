package com.billsAplication.presentation.adapter.image

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.ImageItemBinding
import com.billsAplication.domain.model.ImageItem
import com.bumptech.glide.Glide
import java.util.*
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

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
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
        //Decode String to Bytes
        val decodeImage: ByteArray = Base64.getDecoder().decode(item.stringImage)
//        holder.im_preview.setImageBitmap(BitmapFactory.decodeByteArray(decodeImage,0, decodeImage.size))
        Glide
            .with(holder.itemView.context)
            .load(decodeImage)
            .into(holder.im_preview)
    }

}