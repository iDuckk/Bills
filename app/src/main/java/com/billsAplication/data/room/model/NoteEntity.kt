package com.billsAplication.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_list")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val dateTimeCreation : String,
    val textNote : String,
    val color : String
)