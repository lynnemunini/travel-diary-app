package com.grayseal.traveldiaryapp.data.repository

import com.grayseal.traveldiaryapp.data.database.PhotoDatabaseDao
import com.grayseal.traveldiaryapp.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoDatabaseDao: PhotoDatabaseDao) {
    suspend fun addEntry(photo: Photo) = photoDatabaseDao.insert(photo)
    suspend fun deleteEntry(photo: Photo) =
        photoDatabaseDao.deletePhoto(photo)

    suspend fun getAllEntries(): Flow<List<Photo>> =
        photoDatabaseDao.getPhotoEntries().flowOn(
            Dispatchers.IO
        ).conflate()

}