package com.grayseal.traveldiaryapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grayseal.traveldiaryapp.data.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDatabaseDao {
    /*Return list of photos from database*/
    @Query("SELECT * from photos")
    fun getPhotoEntries():
            Flow<List<Photo>>

    /*Insert an entry to database. If there's any conflict or errors it's replaced with new one*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    /*Delete an entry*/
    @Delete
    suspend fun deletePhoto(photo: Photo)
}