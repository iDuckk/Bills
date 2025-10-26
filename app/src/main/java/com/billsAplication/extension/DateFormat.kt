package com.billsAplication.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

val DATE_FORMAT = "dd/MM/yyyy"
val TIME_FORMAT_12 = "hh:mm a"
val TIME_FORMAT_24 = "HH:mm"

/***
 * DATE
 */

fun getCurrentDateMls() = Calendar.getInstance().timeInMillis

@SuppressLint("SimpleDateFormat")
fun localCurrentDate() = SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().timeInMillis)

@SuppressLint("SimpleDateFormat")
fun setFormattingDate(date: Long?) = SimpleDateFormat(DATE_FORMAT).format(date)

fun convertDateToMonthYear(input: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    val outputFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)

    val date = LocalDate.parse(input, inputFormat)
    return outputFormat.format(date).uppercase()
}


/***
 * TIME
 */

fun getCurrentHour() : Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY)
}

fun getCurrentMinutes() : Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.MINUTE)
}

@SuppressLint("SimpleDateFormat")
fun localCurrentTime() = SimpleDateFormat(TIME_FORMAT_12).format(Calendar.getInstance().time)

fun convertTo12HourFormat(hour: Int, minute: Int): String {
    val time = LocalTime.of(hour, minute)
    val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_12, Locale.ENGLISH)
    return time.format(formatter)
}


fun convertTo12HourFormat(input: String): String {
    if (input.isEmpty()) return ""
    val inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_24)
    val outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_12, Locale.ENGLISH)

    val time = LocalTime.parse(input, inputFormat)
    return time.format(outputFormat)
}

fun convertTo24HourFormat(input: String): String {
    val inputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_12, Locale.ENGLISH)
    val outputFormat = DateTimeFormatter.ofPattern(TIME_FORMAT_24)

    val time = LocalTime.parse(input, inputFormat)
    return time.format(outputFormat)
}

