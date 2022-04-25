package com.billsAplication.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.R
import com.billsAplication.databinding.BillItemBinding
import com.billsAplication.domain.model.BillsItem
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.exp


class BillsAdapter  @Inject constructor(): ListAdapter<BillsItem, BillViewHolder>(BillsListCallback()) {

    private val TYPE_CATEGORY = 2
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1

    private var isClicked = false //If selected item for adapter

    private var mIsHighlight = MutableLiveData<Boolean> ()
    val isHighlight : LiveData<Boolean>
        get() = mIsHighlight //If selected item for billList fragment

    private val electedItemsList: ArrayList<BillsItem> = ArrayList()

    var onClickListenerBillItem: ((item : BillsItem) -> Unit)? = null
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
        holderBill.itemView.setBackgroundResource(R.drawable.background_selector)
        //Choose select item or not
        holderBill.cardVIewTitle.isSelected = electedItemsList.find { it== item } == item
        holderBill.itemView.isSelected = electedItemsList.find { it == item} == item

        if(item.image1.isNotEmpty()
            || item.image2.isNotEmpty()
            || item.image3.isNotEmpty() ||
            item.image4.isNotEmpty() ||
            item.image4.isNotEmpty()
        )
                holderBill.im_ifExist.visibility = View.VISIBLE
        else holderBill.im_ifExist.visibility = View.GONE

        if(item.type != TYPE_CATEGORY) {
            holderBill.tv_Day.text = item.date.dropLast(8)
            holderBill.tv_MonthYear.text = item.date.drop(2)
            holderBill.tv_Time.text = item.time
            holderBill.tv_Category.text = item.category
            holderBill.tv_Item_Description.text = item.note

            if (item.type == TYPE_INCOME) {
                holderBill.tv_Item_Income.visibility = View.VISIBLE
                holderBill.tv_Item_Income.text = item.amount + " " + DecimalFormat().currency!!.currencyCode
                holderBill.tv_Item_Expense.visibility = View.GONE
            } else if(item.type == TYPE_EXPENSES) {
                holderBill.tv_Item_Expense.visibility = View.VISIBLE
                holderBill.tv_Item_Expense.text = item.amount + " " + DecimalFormat().currency!!.currencyCode
                holderBill.tv_Item_Income.visibility = View.GONE
            }else Log.e("TAG", "BillsAdapter: Not found type of bill")
        }

        holderBill.itemView.setOnClickListener {
            if(isClicked)
                highlightItem(item, holderBill)
            else onClickListenerBillItem?.invoke(item)
        }

        holderBill.itemView.setOnLongClickListener {
            onLongClickListenerBillItem?.invoke(electedItemsList)
            highlightItem(item, holderBill)
            true
        }

    }

    private fun highlightItem(
        item: BillsItem,
        holderBill: BillViewHolder
    ) {
        if(electedItemsList.isEmpty()){
            isClicked = true
            mIsHighlight.value = true
        }

        if(electedItemsList.find { it == item} == item) {
            electedItemsList.remove(item)
            holderBill.cardVIewTitle.isSelected = false
            holderBill.itemView.isSelected = false
            if(electedItemsList.isEmpty()) {
                mIsHighlight.value = false
                isClicked = false
            }
        } else {
            electedItemsList.add(item)
            holderBill.cardVIewTitle.isSelected = true
            holderBill.itemView.isSelected = true
        }
    }

    fun deleteItems(){
        electedItemsList.clear()
        mIsHighlight.value = false
        isClicked = false
    }
}