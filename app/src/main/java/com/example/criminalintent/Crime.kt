package com.example.criminalintent

import java.util.*

data class Crime(
    val id: UUID,
    val title: String,
    val date: Date,
    var isSolved: Boolean,
    val requiresPolice: Boolean
)