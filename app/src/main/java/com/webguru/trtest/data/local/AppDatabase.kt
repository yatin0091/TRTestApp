package com.webguru.trtest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webguru.trtest.data.local.dao.PhotoDao
import com.webguru.trtest.data.local.dao.PhotoRemoteKeyDao
import com.webguru.trtest.data.local.dao.TRTestTypeDao
import com.webguru.trtest.data.local.model.PhotoEntity
import com.webguru.trtest.data.local.model.PhotoRemoteKey
import com.webguru.trtest.data.local.model.TRTestType

@Database(entities = [TRTestType::class, PhotoEntity::class, PhotoRemoteKey::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tRTestTypeDao(): TRTestTypeDao

    abstract fun photoDao(): PhotoDao

    abstract fun photoRemoteDao(): PhotoRemoteKeyDao
}