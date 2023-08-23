package com.grayseal.traveldiaryapp.data.repository

import com.grayseal.traveldiaryapp.data.database.DiaryEntriesDatabaseDao
import com.grayseal.traveldiaryapp.data.model.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DiaryEntryRepository @Inject constructor(private val diaryEntriesDatabaseDao: DiaryEntriesDatabaseDao) {
    suspend fun addEntry(diaryEntry: DiaryEntry) = diaryEntriesDatabaseDao.insert(diaryEntry)
    suspend fun updateEntry(diaryEntry: DiaryEntry) = diaryEntriesDatabaseDao.update(diaryEntry)
    suspend fun deleteEntry(diaryEntry: DiaryEntry) =
        diaryEntriesDatabaseDao.deleteDiaryEntry(diaryEntry)

    suspend fun deleteAllEntries() = diaryEntriesDatabaseDao.deleteAll()

    fun getEntryById(entryId: String): Flow<DiaryEntry> =
        diaryEntriesDatabaseDao.getDiaryEntryById(entryId).flowOn(Dispatchers.IO).conflate()

    fun getAllEntries(): Flow<List<DiaryEntry>> =
        diaryEntriesDatabaseDao.getDiaryEntries().flowOn(
            Dispatchers.IO
        ).conflate()
}