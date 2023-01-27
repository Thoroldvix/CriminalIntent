package com.example.criminalintent.util

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


fun Date.formatDateTime(): String {
    val dateTimeFormat = "EEEEdMMMyhmz"
    return DateFormat.getPatternInstance(dateTimeFormat).format(this)
}

fun Date.formatDate(): String {
    val dateFormat = "EEEEdMMMy"
    return DateFormat.getPatternInstance(dateFormat).format(this)

}

fun Date.formatTime(): String {
    val timeFormat = "hmz"
    return DateFormat.getPatternInstance(timeFormat).format(this)
}


