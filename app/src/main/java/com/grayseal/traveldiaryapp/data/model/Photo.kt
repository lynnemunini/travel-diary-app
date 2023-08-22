package com.grayseal.traveldiaryapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "diary_entry_id")
    val diaryEntryId: UUID,

    @ColumnInfo(name = "absolute_path")
    val absolutePath: String
)