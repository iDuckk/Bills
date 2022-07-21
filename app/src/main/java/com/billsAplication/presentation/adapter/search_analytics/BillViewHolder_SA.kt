package com.billsAplication.presentation.adapter.search_analytics

import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.databinding.BillItemBinding
import com.billsAplication.databinding.BillItemSearchBinding

class BillViewHolder_SA(binding: BillItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

    val tv_Day = binding.tvDay
    val tv_MonthYear = binding.tvMonthYear
    val tv_Time = binding.tvTime
    val tv_Category = binding.tvCategory
    val tv_Item_Amaount = binding.tvItemAmount
    val tv_Item_Description = binding.tvItemNote
    val im_ifExist = binding.imIfExist

    val cardVIewTitle = binding.cardViewTitleItem

}
