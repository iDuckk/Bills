package com.billsAplication.data.room.model

import androidx.room.PrimaryKey

data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val dateTimeCreation : String,
    val textNote : String,
    val color : String
)