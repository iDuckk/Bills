package com.billsAplication.domain.model

data class BillsItem(
    val id : Int,
    val type : Int,
    val date : String,
    val category : String,
    val amount : Double,
    val note : String,
    val description : String,
    val urlImage : String
)
