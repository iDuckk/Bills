package com.billsAplication.domain.model

data class NoteItem(
    val id : Int = 0,
    val dateTimeCreation : String,
    val textNote : String,
    val color : String
)