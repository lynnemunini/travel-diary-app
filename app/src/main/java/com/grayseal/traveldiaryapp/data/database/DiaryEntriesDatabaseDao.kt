package com.grayseal.traveldiaryapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntriesDatabaseDao {

    /*Return list of diary entries from database*/
    @Query("SELECT * from diary_entries")
    fun getDiaryEntries():
            Flow<List<DiaryEntry>>

    /*Return diaryEntry with a specific id*/
    @Query("SELECT * from diary_entries where id =:id")
    fun getDiaryEntryById(id: String): Flow<DiaryEntry>

    /*Insert an entry to database. If there's any conflict or errors it's replaced with new one*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaryEntry: DiaryEntry)

    /*Update*/
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(diaryEntry: DiaryEntry)

    /*Delete All*/
    @Query("DELETE from diary_entries")
    suspend fun deleteAll()

    /*Delete an entry*/
    @Delete
    suspend fun deleteDiaryEntry(diaryEntry: DiaryEntry)
}