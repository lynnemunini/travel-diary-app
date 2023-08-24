package com.grayseal.traveldiaryapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID


/**
 * Represents a Diary Entry entity stored in the database.
 *
 * @param id Unique identifier for the Diary Entry, generated using UUID.
 * @param title Title of the Diary Entry.
 * @param date Date associated with the Diary Entry. Defaults to the current time.
 * @param location Location associated with the Diary Entry.
 * @param notes Notes or content of the Diary Entry.
 */
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date")
    val date: String = Calendar.getInstance().time.toString(),

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "notes")
    val notes: String,
)
