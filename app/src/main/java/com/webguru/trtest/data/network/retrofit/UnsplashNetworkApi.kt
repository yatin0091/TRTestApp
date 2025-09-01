package com.webguru.trtest.data.network.retrofit

import com.webguru.trtest.data.network.model.NetworkPhoto
import retrofit2.Response
import retrofit2.http.GET

interface UnsplashNetworkApi {
    @GET("photos/")
    suspend fun getPhotos(): List<NetworkPhoto>
}