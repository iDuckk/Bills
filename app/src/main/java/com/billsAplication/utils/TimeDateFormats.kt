package com.billsAplication.utils

import java.text.SimpleDateFormat
import java.util.Calendar


fun timeFormat(time: String): String {
    var hour = "${time.get(0)}${time.get(1)}".toInt()
    var minute = "${time.get(3)}${time.get(4)}".toInt()

    val c = Calendar.getInstance()
    c.set(Calendar.HOUR_OF_DAY, hour)
    c.set(Calendar.MINUTE, minute)

    return SimpleDateFormat("hh:mm a").format(c.time)
}