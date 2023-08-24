package com.grayseal.traveldiaryapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents a Photo entity stored in the database, associated with a Diary Entry.
 *
 * @param id Unique identifier for the Photo, generated using UUID.
 * @param diaryEntryId ID of the Diary Entry associated with this Photo.
 * @param absolutePath Absolute path to the photo file.
 */
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "diary_entry_id")
    val diaryEntryId: String,

    @ColumnInfo(name = "absolute_path")
    val absolutePath: String
)
