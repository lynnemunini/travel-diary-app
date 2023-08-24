package com.grayseal.traveldiaryapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo

/**
 * Room database class for managing Diary Entry and Photo data.
 *
 * @property entities The array of entity classes managed by this database.
 * @property version The version of the database schema.
 * @property exportSchema Whether to export the schema to a schema file.
 */
@Database(entities = [DiaryEntry::class, Photo::class], version = 2, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {

    /**
     * Provides an instance of the DiaryEntriesDatabaseDao interface for accessing Diary Entry data in the database.
     *
     * @return The DiaryEntriesDatabaseDao instance.
     */
    abstract fun diaryEntriesDao(): DiaryEntriesDatabaseDao

    /**
     * Provides an instance of the PhotoDatabaseDao interface for accessing Photo data in the database.
     *
     * @return The PhotoDatabaseDao instance.
     */
    abstract fun photosDao(): PhotoDatabaseDao
}
