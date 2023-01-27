package com.example.criminalintent.util

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


fun Date.formatDateTime(): String {
    return DateFormat.getPatternInstance("EEEEdMMMyhmz").format(this)
}

fun Date.formatDate(): String {
    return DateFormat.getPatternInstance("EEEEdMMMy").format(this)

}

fun Date.formatTime(): String {
    return DateFormat.getPatternInstance("hmz").format(this)
}


