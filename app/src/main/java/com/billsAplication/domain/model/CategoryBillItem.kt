package com.billsAplication.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryBillItem(
    val id: Int,
    val typeCategory: Int,
    val category: String
) : Parcelable
