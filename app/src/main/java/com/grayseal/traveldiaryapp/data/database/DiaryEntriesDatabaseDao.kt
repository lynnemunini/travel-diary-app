package com.grayseal.traveldiaryapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for Diary Entry data.
 */
@Dao
interface DiaryEntriesDatabaseDao {

    /**
     * Retrieves a list of all Diary Entries from the database.
     *
     * @return A Flow of a list of DiaryEntry objects.
     */
    @Query("SELECT * from diary_entries")
    fun getDiaryEntries(): Flow<List<DiaryEntry>>

    /**
     * Retrieves a specific Diary Entry by its ID.
     *
     * @param id The ID of the Diary Entry to retrieve.
     * @return A Flow of the requested DiaryEntry object.
     */
    @Query("SELECT * from diary_entries where id = :id")
    fun getDiaryEntryById(id: String): Flow<DiaryEntry>

    /**
     * Inserts a new Diary Entry into the database or replaces an existing entry if a conflict occurs.
     *
     * @param diaryEntry The Diary Entry object to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaryEntry: DiaryEntry)

    /**
     * Updates an existing Diary Entry in the database.
     *
     * @param diaryEntry The Diary Entry object to update.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(diaryEntry: DiaryEntry)

    /**
     * Deletes all Diary Entries from the database.
     */
    @Query("DELETE from diary_entries")
    suspend fun deleteAll()

    /**
     * Deletes a specific Diary Entry from the database.
     *
     * @param diaryEntry The Diary Entry object to delete.
     */
    @Delete
    suspend fun deleteDiaryEntry(diaryEntry: DiaryEntry)
}
