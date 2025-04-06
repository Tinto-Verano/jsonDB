package com.example.jsondb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val IMEI: String,
    val AP: String,
    val CAMERA: String,
    val OCTA: String
)