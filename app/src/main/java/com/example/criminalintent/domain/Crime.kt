package com.example.criminalintent.domain

import java.util.*

data class Crime(
    val id: UUID,
    val title: String,
    val date: Date,
    var isSolved: Boolean,
    val requiresPolice: Boolean
)