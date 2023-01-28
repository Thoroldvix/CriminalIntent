package com.example.criminalintent.viewmodels



import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope

import com.example.criminalintent.database.Crime

import com.example.criminalintent.repository.CrimeRepository

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch


private const val TAG = "CrimeListViewModel"


class CrimeListViewModel : ViewModel() {


    private val crimeRepository = CrimeRepository.get()

    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())

    init {

        viewModelScope.launch {

            crimeRepository.getCrimes().collect {

                _crimes.value = it

            }

        }

    }


    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()

    suspend fun addCrime(crime: Crime) {

        crimeRepository.addCrime(crime)

    }


}


