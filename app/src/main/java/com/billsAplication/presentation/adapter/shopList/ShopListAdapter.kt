package com.billsAplication.presentation.adapter.shopList

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.R
import com.billsAplication.databinding.BookmarksItemBinding
import com.billsAplication.databinding.NoteItemBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.ShopListItem
import javax.inject.Inject


class ShopListAdapter  @Inject constructor(): ListAdapter<ShopListItem, ShopListViewHolder>(
    ShopListCallback()
) {

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

        holder.tv_note.text = item.note

        //set background here because it doesn't set in XML
//        holder.cardVIew.setBackgroundResource(R.drawable.background_selector)
    }

}