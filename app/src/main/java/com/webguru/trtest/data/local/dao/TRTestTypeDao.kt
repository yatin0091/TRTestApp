package com.webguru.trtest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.webguru.trtest.data.local.model.TRTestType
import kotlinx.coroutines.flow.Flow

@Dao
interface TRTestTypeDao {
    @Query("SELECT * FROM trtesttype ORDER BY uid DESC LIMIT 10")
    fun getTRTestTypes(): Flow<List<TRTestType>>

    @Insert
    suspend fun insertTRTestType(item: TRTestType)
}
