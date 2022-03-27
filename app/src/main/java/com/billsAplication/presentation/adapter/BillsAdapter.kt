package com.billsAplication.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.billsAplication.databinding.BillItemBinding
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

@ApplicationScope
class BillsAdapter  @Inject constructor(): ListAdapter<BillsItem, BillViewHolder>(BillsListCallback()) { //: ListAdapter<BillsItem, ViewHolder>(BillsListCallback())

    private val TYPE_CATEGORY = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        return BillViewHolder(
            BillItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holderBill: BillViewHolder, position: Int) {
        val item = getItem(position)

        if(item.type != TYPE_CATEGORY) {
            holderBill.tv_Day.text = item.date.dropLast(8)
            holderBill.tv_MonthYear.text = item.date.drop(2)
            holderBill.tv_Time.text = item.time
            holderBill.tv_Category.text = item.category
            holderBill.tv_Item_Description.text = item.note

            if (item.amount > 0) {
                holderBill.tv_Item_Income.text = item.amount.toString()
            } else {
                holderBill.tv_Item_Expense.text = item.amount.toString()
            }
        }

    }

}