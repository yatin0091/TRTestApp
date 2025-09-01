package com.webguru.trtest.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.webguru.trtest.data.model.Photo

@JsonClass(generateAdapter = true)
data class NetworkPhoto(
    val id: String,
    @Json(name = "blur_hash")
    val blurHash: String,
    val color: String,
    @Json(name = "created_at")
    val createdAt: String,
    val description: String?,
    val height: Int,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    val likes: Int,
    @Json(name = "updated_at")
    val updatedAt: String,
    val urls: Urls,
    val width: Int
)

@JsonClass(generateAdapter = true)
data class Urls(
    val full: String,
    val raw: String,
    val regular: String,
    val small: String,
    val thumb: String
)

fun NetworkPhoto.toPhoto() = Photo(id = id, smallUrl = urls.small, bigImageUrl = urls.full)
