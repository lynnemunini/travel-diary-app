package com.grayseal.traveldiaryapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "diary_entry_id")
    val diaryEntryId: String,

    @ColumnInfo(name = "absolute_path")
    val absolutePath: String
)