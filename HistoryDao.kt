package com.example.jsondb

import androidx.room.*

@Dao
interface HistoryDao {
    @Insert suspend fun insert(entity: HistoryEntity)
    @Query("SELECT * FROM history") suspend fun getAll(): List<HistoryEntity>
    @Query("SELECT * FROM history ORDER BY id DESC LIMIT 1") suspend fun getLatest(): HistoryEntity?
    @Query("DELETE FROM history") suspend fun clear()
    @Query("SELECT COUNT(*) FROM history") suspend fun isEmpty(): Boolean
}
