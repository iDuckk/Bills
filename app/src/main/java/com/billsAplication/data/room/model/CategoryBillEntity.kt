package com.billsAplication.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories_list")
data class CategoryBillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val typeCategory: Int,
    val category: String
)
