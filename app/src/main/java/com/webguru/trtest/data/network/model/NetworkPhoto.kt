package com.webguru.trtest.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.webguru.trtest.data.model.Photo

@JsonClass(generateAdapter = true)
data class NetworkPhoto(
    val id: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    val width: Long,
    val height: Long,
    val color: String,
    @Json(name = "blur_hash")
    val blurHash: String,
    val likes: Long,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    val description: String,
    val urls: Urls,
)

fun NetworkPhoto.toPhoto() = Photo(smallUrl = urls.small, bigImageUrl = urls.full)

data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
)