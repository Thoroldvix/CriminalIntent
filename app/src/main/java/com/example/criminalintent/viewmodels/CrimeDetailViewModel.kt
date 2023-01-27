package com.example.criminalintent.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.criminalintent.database.Crime
import com.example.criminalintent.repository.CrimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeDetailViewModel(crimeId: UUID) : ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()

    init {
        viewModelScope.launch {
            _crime.value = crimeRepository.getCrime(crimeId)
        }
    }

    fun updateCrime(onUpdate: (Crime) -> Crime) {
        _crime.update { oldCrime ->
            oldCrime?.let {
                onUpdate(it)
            }
        }
    }
    suspend fun deleteCrime() {
        crime.value?.let { crimeRepository.deleteCrime(it) }
    }
    fun checkIfCrimeTitleIsEmpty(): Boolean {
        crime.value?.let {
            if (it.title.isEmpty()) {
                return true
            }
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        crime.value?.let { crimeRepository.updateCrime(it) }
    }
}


class CrimeDetailViewModelFactory(
    private val crimeId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(crimeId) as T
    }
}