package com.billsAplication.domain.model

data class BillsItem(
    val id : Int,
    val type : Int,
    val month : String,
    val date : String,
    val time : String,
    val category : String,
    val amount : Double,
    val note : String,
    val description : String,
    val bookmark : Boolean,
    val image1 : String,
    val image2 : String,
    val image3 : String,
    val image4 : String,
    val image5 : String
)
