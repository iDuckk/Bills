package com.billsAplication.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.R
import com.billsAplication.databinding.BookmarksItemBinding
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject


class BookmarksAdapter  @Inject constructor(): ListAdapter<BillsItem, BookmarksViewHolder>(BookmarksListCallback()) {

    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1

    private var isClicked = false //If selected item for adapter

    private var mIsHighlight = MutableLiveData<Boolean> ()
    val isHighlight : LiveData<Boolean>
        get() = mIsHighlight //If selected item for billList fragment

    private val electedItemsList: ArrayList<BillsItem> = ArrayList()

    var onClickListenerBookmarkItem: ((item : BillsItem) -> Unit)? = null
    var onLongClickListenerBookmarkItem: ((ArrayList<BillsItem>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        return BookmarksViewHolder(
            BookmarksItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val item = getItem(position)
//        //set background here because it doesn't set in XML
//        holder.cardVIew.setBackgroundResource(R.drawable.background_selector)
//        holder.itemView.setBackgroundResource(R.drawable.background_selector)
//        //Choose select item or not
//        holder.cardVIew.isSelected = electedItemsList.find { it== item } == item
//        holder.itemView.isSelected = electedItemsList.find { it == item} == item

        holder.tv_category.text = item.category
        holder.tv_item_note.text = item.note
        holder.tv_item_amount.text = item.amount
        when (item.type) {
            TYPE_INCOME -> holder.tv_item_amount.setTextColor(R.color.text_income)
            TYPE_EXPENSES -> holder.tv_item_amount.setTextColor(R.color.text_expense)
            else -> Log.e("TAG", "BookmarksAdapter: Not found type of bill")
        }


        holder.itemView.setOnClickListener {
//            if(isClicked)
//                highlightItem(item, holder)
//            else
                onClickListenerBookmarkItem?.invoke(item)
        }
//
//        holder.itemView.setOnLongClickListener {
//            onLongClickListenerBookmarkItem?.invoke(electedItemsList)
//            highlightItem(item, holder)
//            true
//        }

    }

    private fun highlightItem(
        item: BillsItem,
        holderBill: BookmarksViewHolder
    ) {
        if(electedItemsList.isEmpty()){
            isClicked = true
            mIsHighlight.value = true
        }

        if(electedItemsList.find { it == item} == item) {
            electedItemsList.remove(item)
            holderBill.cardVIew.isSelected = false
            holderBill.itemView.isSelected = false
            if(electedItemsList.isEmpty()) {
                mIsHighlight.value = false
                isClicked = false
            }
        } else {
            electedItemsList.add(item)
            holderBill.cardVIew.isSelected = true
            holderBill.itemView.isSelected = true
        }
    }

    fun deleteItems(){
        electedItemsList.clear()
        mIsHighlight.value = false
        isClicked = false
    }
}