package com.billsAplication.presentation.adapter.shopList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.NoteItemBinding
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject


class ShopListAdapter  @Inject constructor(): ListAdapter<BillsItem, ShopListViewHolder>(
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