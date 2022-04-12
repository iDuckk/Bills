package com.billsAplication.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.databinding.BillItemBinding

class BillViewHolder( binding: BillItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val tv_Day = binding.tvDay
    val tv_MonthYear = binding.tvMonthYear
    val tv_Time = binding.tvTime
    val tv_Category = binding.tvCategory
    val tv_Item_Income = binding.tvItemIncome
    val tv_Item_Expense = binding.tvItemExpense
    val tv_Item_Description = binding.tvItemNote
    val im_ifExist = binding.imIfExist

}
