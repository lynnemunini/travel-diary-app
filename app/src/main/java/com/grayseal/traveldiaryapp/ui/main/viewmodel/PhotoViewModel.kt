package com.grayseal.traveldiaryapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for managing photos using Dagger Hilt.
 *
 * @param repository The repository providing data access methods for photos.
 */
@HiltViewModel
class PhotoViewModel @Inject constructor(private val repository: PhotoRepository) : ViewModel() {

    /**
     * Adds a new photo entry to the repository.
     *
     * @param photo The photo object to be added.
     */
    fun addEntry(photo: Photo) {
        // Launch a coroutine in the IO dispatcher to perform the add operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEntry(photo)
        }
    }

    /**
     * Deletes a photo entry from the repository.
     *
     * @param photo The photo object to be deleted.
     */
    fun deleteEntry(photo: Photo) {
        // Launch a coroutine in the IO dispatcher to perform the delete operation.
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(photo)
        }
    }

    /**
     * Retrieves all photo entries as a Flow from the repository.
     *
     * @return A Flow emitting a list of photos.
     */
    fun getAllEntries(): Flow<List<Photo>> {
        return repository.getAllEntries()
    }
}