package com.grayseal.traveldiaryapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID


@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date")
    val date: String = Calendar.getInstance().time.toString(),

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "notes")
    val notes: String,
)