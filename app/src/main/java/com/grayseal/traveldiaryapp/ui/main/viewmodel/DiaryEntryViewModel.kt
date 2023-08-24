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

/**
 * ViewModel class for managing diary entries using Dagger Hilt.
 *
 * @param repository The repository providing data access methods for diary entries.
 */
@HiltViewModel
class DiaryEntryViewModel @Inject constructor(private val repository: DiaryEntryRepository) :
    ViewModel() {

    /**
     * Adds a new diary entry to the repository.
     *
     * @param diaryEntry The diary entry object to be added.
     */
    fun addEntry(diaryEntry: DiaryEntry) {
        // Launch a coroutine in the IO dispatcher to perform the add operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEntry(diaryEntry)
        }
    }

    /**
     * Updates an existing diary entry in the repository.
     *
     * @param diaryEntry The diary entry object to be updated.
     */
    fun updateEntry(diaryEntry: DiaryEntry) {
        // Launch a coroutine in the IO dispatcher to perform the update operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEntry(diaryEntry)
        }
    }

    /**
     * Deletes a diary entry from the repository.
     *
     * @param diaryEntry The diary entry object to be deleted.
     */
    fun deleteEntry(diaryEntry: DiaryEntry) {
        // Launch a coroutine in the IO dispatcher to perform the delete operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(diaryEntry)
        }
    }

    /**
     * Deletes all diary entries from the repository.
     */
    fun deleteAllEntries() {
        // Launch a coroutine in the IO dispatcher to perform the delete all operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntries()
        }
    }

    /**
     * Retrieves a diary entry by its ID as a Flow from the repository.
     *
     * @param entryId The ID of the desired diary entry.
     * @return A Flow emitting the requested diary entry.
     */
    fun getEntryById(entryId: String): Flow<DiaryEntry> = repository.getEntryById(entryId)

    /**
     * Retrieves all diary entries as a Flow from the repository.
     *
     * @return A Flow emitting a list of diary entries.
     */
    fun getAllEntries(): Flow<List<DiaryEntry>> {
        return repository.getAllEntries()
    }
}
