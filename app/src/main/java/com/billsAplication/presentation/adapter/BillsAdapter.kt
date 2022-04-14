package com.billsAplication.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.BillItemBinding
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.model.BillsItem
import java.text.DecimalFormat
import javax.inject.Inject

@ApplicationScope
class BillsAdapter  @Inject constructor(): ListAdapter<BillsItem, BillViewHolder>(BillsListCallback()) { //: ListAdapter<BillsItem, ViewHolder>(BillsListCallback())

    private val TYPE_CATEGORY = 2
    private val TYPE_EXPENSES = 0
    private val TYPE_INCOME = 1

    var onClickListenerBillItem: ((item : BillsItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        return BillViewHolder(
            BillItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holderBill: BillViewHolder, position: Int) {
        val item = getItem(position)

        if(!item.image1.isNullOrEmpty()
            ||!item.image2.isNullOrEmpty()
            ||!item.image3.isNullOrEmpty()||
            !item.image4.isNullOrEmpty()||
            !item.image4.isNullOrEmpty())
                holderBill.im_ifExist.visibility = View.VISIBLE
        else holderBill.im_ifExist.visibility = View.GONE

        if(item.type != TYPE_CATEGORY) {
            holderBill.tv_Day.text = item.date.dropLast(8)
            holderBill.tv_MonthYear.text = item.date.drop(2)
            holderBill.tv_Time.text = item.time
            holderBill.tv_Category.text = item.category
            holderBill.tv_Item_Description.text = item.note

            if (item.type == TYPE_INCOME) {
                holderBill.tv_Item_Income
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
            onClickListenerBillItem?.invoke(item)
        }

    }

}