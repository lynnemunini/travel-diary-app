package com.grayseal.traveldiaryapp.data.repository

import com.grayseal.traveldiaryapp.data.database.PhotoDatabaseDao
import com.grayseal.traveldiaryapp.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Repository class that serves as an interface between the ViewModel and the underlying data source.
 * Handles CRUD operations and retrieval of Photo data.
 *
 * @param photoDatabaseDao The data access object for Photo entities.
 */
class PhotoRepository @Inject constructor(private val photoDatabaseDao: PhotoDatabaseDao) {
    /**
     * Adds a new Photo to the database.
     *
     * @param photo The Photo instance to be added.
     */
    suspend fun addEntry(photo: Photo) = photoDatabaseDao.insert(photo)

    /**
     * Deletes a Photo from the database.
     *
     * @param photo The Photo instance to be deleted.
     */
    suspend fun deleteEntry(photo: Photo) =
        photoDatabaseDao.deletePhoto(photo)

    /**
     * Retrieves all Photo entries from the database.
     *
     * @return A Flow representing a list of all Photo entries in the database.
     */
    fun getAllEntries(): Flow<List<Photo>> =
        photoDatabaseDao.getPhotoEntries().flowOn(Dispatchers.IO).conflate()
}
