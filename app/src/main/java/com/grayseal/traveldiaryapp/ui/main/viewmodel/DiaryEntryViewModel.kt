package com.grayseal.traveldiaryapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.repository.DiaryEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryEntryViewModel @Inject constructor (private val repository: DiaryEntryRepository) : ViewModel() {

    fun addEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEntry(diaryEntry)
        }
    }

    fun updateEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEntry(diaryEntry)
        }
    }

    fun deleteEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(diaryEntry)
        }
    }

    fun deleteAllEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntries()
        }
    }

    fun getAllEntries(): Flow<List<DiaryEntry>> {
        return repository.getAllEntries()
    }
}
