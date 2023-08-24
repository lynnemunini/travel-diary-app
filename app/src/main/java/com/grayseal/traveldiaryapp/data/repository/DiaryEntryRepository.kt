package com.grayseal.traveldiaryapp.data.repository

import com.grayseal.traveldiaryapp.data.database.DiaryEntriesDatabaseDao
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Repository class that acts as an interface between the ViewModel and the underlying data source.
 * Handles CRUD operations and retrieval of DiaryEntry data.
 *
 * @param diaryEntriesDatabaseDao The data access object for DiaryEntry entities.
 */
class DiaryEntryRepository @Inject constructor(private val diaryEntriesDatabaseDao: DiaryEntriesDatabaseDao) {
    /**
     * Adds a new DiaryEntry to the database.
     *
     * @param diaryEntry The DiaryEntry instance to be added.
     */
    suspend fun addEntry(diaryEntry: DiaryEntry) = diaryEntriesDatabaseDao.insert(diaryEntry)

    /**
     * Updates an existing DiaryEntry in the database.
     *
     * @param diaryEntry The DiaryEntry instance with updated data.
     */
    suspend fun updateEntry(diaryEntry: DiaryEntry) = diaryEntriesDatabaseDao.update(diaryEntry)

    /**
     * Deletes a DiaryEntry from the database.
     *
     * @param diaryEntry The DiaryEntry instance to be deleted.
     */
    suspend fun deleteEntry(diaryEntry: DiaryEntry) =
        diaryEntriesDatabaseDao.deleteDiaryEntry(diaryEntry)

    /**
     * Deletes all DiaryEntries from the database.
     */
    suspend fun deleteAllEntries() = diaryEntriesDatabaseDao.deleteAll()

    /**
     * Retrieves a specific DiaryEntry by its unique ID.
     *
     * @param entryId The unique ID of the DiaryEntry to be retrieved.
     * @return A Flow representing the DiaryEntry with the specified ID.
     */
    fun getEntryById(entryId: String): Flow<DiaryEntry> =
        diaryEntriesDatabaseDao.getDiaryEntryById(entryId).flowOn(Dispatchers.IO).conflate()

    /**
     * Retrieves all DiaryEntries from the database.
     *
     * @return A Flow representing a list of all DiaryEntries in the database.
     */
    fun getAllEntries(): Flow<List<DiaryEntry>> =
        diaryEntriesDatabaseDao.getDiaryEntries().flowOn(Dispatchers.IO).conflate()
}
