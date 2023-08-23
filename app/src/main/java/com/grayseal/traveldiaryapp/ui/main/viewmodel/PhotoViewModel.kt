package com.grayseal.traveldiaryapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.traveldiaryapp.data.model.Photo
import com.grayseal.traveldiaryapp.data.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PhotoViewModel(private val repository: PhotoRepository) : ViewModel() {

    fun addEntry(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEntry(photo)
        }
    }

    fun deleteEntry(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(photo)
        }
    }

    suspend fun getAllEntries(): Flow<List<Photo>> {
        return repository.getAllEntries()
    }
}