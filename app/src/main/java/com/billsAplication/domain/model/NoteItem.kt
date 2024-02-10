package com.billsAplication.domain.model

import java.time.LocalDateTime
import java.util.TimeZone

data class NoteItem(
    val id : Int = 0,
    val dateTimeCreation : String = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId()).toString(),
    val textNote : String,
    val color : String
)