package com.webguru.trtest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.webguru.trtest.data.local.model.PhotoRemoteKey

@Dao
interface PhotoRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(keys: List<PhotoRemoteKey>)

    @Query("SELECT * FROM photo_remote_keys WHERE id = :id")
    suspend fun remoteKeyById(id: String): PhotoRemoteKey?

    @Query("DELETE FROM photo_remote_keys")
    suspend fun clearAll()
}