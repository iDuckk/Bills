package com.billsAplication.presentation.adapter.bills

import android.R.attr.left
import android.R.attr.right
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.R
import com.billsAplication.databinding.BillItemBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.CurrentCurrency
import javax.inject.Inject


class BillsAdapter @Inject constructor() :
    ListAdapter<BillsItem, BillViewHolder>(BillsListCallback()) {

    private val TYPE_CATEGORY = 2
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1
    private val TYPE_NOTE = 3


    private var isClicked = false //If selected item for adapter

    private var mIsHighlight = MutableLiveData<Boolean>()
    val isHighlight: LiveData<Boolean>
        get() = mIsHighlight //If selected item for billList fragment

    private val electedItemsList: ArrayList<BillsItem> = ArrayList()

    var onClickListenerBillItem: ((item: BillsItem) -> Unit)? = null
    var onLongClickListenerBillItem: ((ArrayList<BillsItem>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        return BillViewHolder(
            BillItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holderBill: BillViewHolder, position: Int) {
        val item = getItem(position)
        //set background here because it doesn't set in XML
        holderBill.cardVIewTitle.setBackgroundResource(R.drawable.background_selector)
        holderBill.cardVIewDate.setBackgroundResource(R.drawable.date_border_cardview)
//        holderBill.itemView.setBackgroundResource(R.drawable.background_selector)
        //Choose select item or not
        holderBill.cardVIewTitle.isSelected = electedItemsList.find { it == item } == item
        holderBill.itemView.isSelected = electedItemsList.find { it == item } == item

        if (item.image1.isNotEmpty()
            || item.image2.isNotEmpty()
            || item.image3.isNotEmpty() ||
            item.image4.isNotEmpty() ||
            item.image4.isNotEmpty()
        )
            holderBill.im_ifExist.visibility = View.VISIBLE
        else holderBill.im_ifExist.visibility = View.GONE

        if (item.type != TYPE_CATEGORY && item.type != TYPE_NOTE) {
            //Margin cardView if date the same
            setCardView(position, item, holderBill)
            //set Values
            holderBill.tv_Day.text = item.date.dropLast(8)
            holderBill.tv_MonthYear.text = item.date.drop(2)
            holderBill.tv_Time.text = item.time
            holderBill.tv_Category.text = item.category
            holderBill.tv_Item_Description.text = item.note
            //set amount
            if (item.type == TYPE_INCOME) {
                holderBill.tv_Item_Amaount.text = item.amount + " " + CurrentCurrency.currency
                holderBill.tv_Item_Amaount.setTextColor(
                    holderBill.itemView.context.getColor(R.color.text_income)
                )
            } else if (item.type == TYPE_EXPENSES) {
                holderBill.tv_Item_Amaount.text = item.amount + " " + CurrentCurrency.currency
                holderBill.tv_Item_Amaount.setTextColor(
                    holderBill.itemView.context.getColor(R.color.text_expense)
                )
            } else Log.e(
                "TAG",
                holderBill.itemView.context.getString(R.string.attention_billsAdapter_notfoundType)
            )
        }

        holderBill.itemView.setOnClickListener {
            if (isClicked)
                highlightItem(item, holderBill)
            else onClickListenerBillItem?.invoke(item)
        }

        holderBill.itemView.setOnLongClickListener {
            onLongClickListenerBillItem?.invoke(electedItemsList)
            highlightItem(item, holderBill)
            true
        }

    }

    private fun setCardView(
        position: Int,
        item: BillsItem,
        holderBill: BillViewHolder
    ) {
        //Set only TIME, cause DATE have set yet
        if (position != 0 && item.date == getItem(position - 1).date) {
            holderBill.cardVIewDate.visibility = View.GONE
            //Set Margin card
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 1)
            holderBill.itemView.layoutParams = params

        } else {//Set Margin card
            holderBill.cardVIewDate.visibility = View.VISIBLE
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(1, 15, 1, 1)
            holderBill.itemView.layoutParams = params
        }
    }

    private fun highlightItem(
        item: BillsItem,
        holderBill: BillViewHolder
    ) {
        if (electedItemsList.isEmpty()) {
            isClicked = true
            mIsHighlight.value = true
        }

        if (electedItemsList.find { it == item } == item) {
            electedItemsList.remove(item)
            holderBill.cardVIewTitle.isSelected = false
            holderBill.itemView.isSelected = false
            if (electedItemsList.isEmpty()) {
                mIsHighlight.value = false
                isClicked = false
            }
        } else {
            electedItemsList.add(item)
            holderBill.cardVIewTitle.isSelected = true
            holderBill.itemView.isSelected = true
        }
    }

    fun deleteItemsAfterRemovedItemFromDB() {
        electedItemsList.clear()
        mIsHighlight.value = false
        isClicked = false
    }

}