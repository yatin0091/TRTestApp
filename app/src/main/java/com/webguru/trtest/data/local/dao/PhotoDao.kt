package com.webguru.trtest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.webguru.trtest.data.local.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY dbId ASC") // or any stable sort (createdAt if available)
    fun observeAll(): Flow<List<PhotoEntity>>

    @Upsert
    suspend fun upsertAll(items: List<PhotoEntity>)

    @Query("SELECT COUNT(*) FROM photos")
    suspend fun count(): Int

    @Query("DELETE FROM photos")
    suspend fun clearAll()
}

