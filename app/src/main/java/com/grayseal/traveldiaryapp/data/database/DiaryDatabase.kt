package com.grayseal.traveldiaryapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import com.grayseal.traveldiaryapp.data.model.Photo

@Database(entities = [DiaryEntry::class, Photo::class], version = 2, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {
    // Function to give DAO
    abstract fun diaryEntriesDao(): DiaryEntriesDatabaseDao
    abstract fun photosDao(): PhotoDatabaseDao
}