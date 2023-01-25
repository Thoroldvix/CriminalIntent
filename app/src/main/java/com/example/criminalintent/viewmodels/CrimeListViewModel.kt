package com.example.criminalintent.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.database.Crime
import com.example.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "CrimeListViewModel"

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimes = mutableListOf<Crime>()

    init {
        viewModelScope.launch {
            crimes += loadCrimes()
        }
    }

    suspend fun loadCrimes(): List<Crime> {
        return crimeRepository.getCrimes()
    }
}