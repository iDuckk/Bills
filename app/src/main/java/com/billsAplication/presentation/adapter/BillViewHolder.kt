package com.billsAplication.presentation.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.R

class BillViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {

    val tv_Day = view.findViewById<TextView>(R.id.tv_Day)
    val tv_MonthYear = view.findViewById<TextView>(R.id.tv_MonthYear)
    val tv_Time = view.findViewById<TextView>(R.id.tv_Time)
    val tv_Category = view.findViewById<TextView>(R.id.tv_Category)
    val tv_Item_Income = view.findViewById<TextView>(R.id.tv_Item_Income)
    val tv_Item_Expense = view.findViewById<TextView>(R.id.tv_Item_Expense)
    val tv_Item_Description = view.findViewById<TextView>(R.id.tv_Item_Description)

}
