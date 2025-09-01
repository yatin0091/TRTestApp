package com.webguru.trtest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webguru.trtest.data.local.dao.TRTestTypeDao
import com.webguru.trtest.data.local.model.TRTestType

@Database(entities = [TRTestType::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tRTestTypeDao(): TRTestTypeDao
}