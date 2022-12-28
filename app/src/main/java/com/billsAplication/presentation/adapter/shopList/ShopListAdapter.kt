package com.billsAplication.presentation.adapter.shopList

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.LinkMovementMethod
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
            COLOR_NOTE_BLUE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue_a100))
            COLOR_NOTE_RED -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red_a100))
            COLOR_NOTE_YELLOW -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.yellow_600))
            COLOR_NOTE_GREEN -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_a200))
            COLOR_NOTE_ORANGE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.orange_600))
            COLOR_NOTE_PURPLE -> holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.deep_purple_a100))
            COLOR_NOTE_PRIMARY -> holder.itemView.setBackgroundColor(holder.itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating))
        }

        holder.tv_note.text = item.note
        holder.tv_note.setLinkTextColor(holder.itemView.context.getColor(R.color.text_income))

        holder.im_edit.setOnClickListener {
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