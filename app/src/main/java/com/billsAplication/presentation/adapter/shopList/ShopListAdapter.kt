package com.billsAplication.presentation.adapter.shopList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.R
import com.billsAplication.databinding.NoteItemBinding
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject


class ShopListAdapter  @Inject constructor(): ListAdapter<BillsItem, ShopListViewHolder>(
    ShopListCallback()
) {
    private val COLOR_NOTE_BLUE = "blue"
    private val COLOR_NOTE_RED = "red"
    private val COLOR_NOTE_ORANGE = "orange"
    private val COLOR_NOTE_GREEN = "green"
    private val COLOR_NOTE_YELLOW = "yellow"
    private val COLOR_NOTE_PURPLE = "purple"
    private val COLOR_NOTE_PRIMARY = ""

//    private val TYPE_EXPENSES = 0

    var onClickListenerShopListItem: ((item : BillsItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        return ShopListViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val item = getItem(position)

        when(item.description){
            COLOR_NOTE_BLUE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.sky_blue))
            COLOR_NOTE_RED -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_coral))
            COLOR_NOTE_YELLOW -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.khaki))
            COLOR_NOTE_GREEN -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_yellow))
            COLOR_NOTE_ORANGE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.andy_brown))
            COLOR_NOTE_PURPLE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.plum))
            COLOR_NOTE_PRIMARY -> holder.itemView.setBackgroundColor(holder.itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        }

        holder.tv_note.text = item.note

        holder.itemView.setOnClickListener {
            onClickListenerShopListItem?.invoke(item)
        }

        //set background here because it doesn't set in XML
//        holder.cardVIew.setBackgroundResource(R.drawable.background_selector)
    }

    @ColorInt
    fun Context.getColorFromAttr(@AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

}