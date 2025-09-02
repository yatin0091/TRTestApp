package com.webguru.trtest.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.webguru.trtest.data.model.Photo

@Entity(tableName = "photos", indices = [Index(value = ["id"], unique = true)])
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val dbId: Long = 0L,
    val id: String,
    val smallUrl: String,
    val fullUrl: String
)

fun PhotoEntity.toPhoto(): Photo = Photo(id, smallUrl, fullUrl)

