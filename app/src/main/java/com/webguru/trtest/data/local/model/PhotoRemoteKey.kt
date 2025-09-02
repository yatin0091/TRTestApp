package com.webguru.trtest.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_remote_keys")
data class PhotoRemoteKey(
    @PrimaryKey val id: String,   // same as photo id
    val prevKey: Int?,
    val nextKey: Int?
)
