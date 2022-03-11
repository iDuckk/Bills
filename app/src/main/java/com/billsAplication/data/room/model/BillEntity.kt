package com.billsAplication.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills_list")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val type : Int,
    val month : String,
    val date : String,
    val time : String,
    val category : String,
    val amount : Double,
    val note : String,
    val description : String,
    val urlImage : String
)
