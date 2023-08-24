package com.grayseal.traveldiaryapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grayseal.traveldiaryapp.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for Photo data.
 */
@Dao
interface PhotoDatabaseDao {

    /**
     * Retrieves a list of all Photos from the database.
     *
     * @return A Flow of a list of Photo objects.
     */
    @Query("SELECT * from photos")
    fun getPhotoEntries(): Flow<List<Photo>>

    /**
     * Inserts a new Photo into the database or replaces an existing entry if a conflict occurs.
     *
     * @param photo The Photo object to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    /**
     * Deletes a specific Photo from the database.
     *
     * @param photo The Photo object to delete.
     */
    @Delete
    suspend fun deletePhoto(photo: Photo)
}
