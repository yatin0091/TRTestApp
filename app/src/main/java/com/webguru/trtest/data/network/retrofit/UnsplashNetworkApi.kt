package com.webguru.trtest.data.network.retrofit

import com.webguru.trtest.data.network.model.NetworkPhoto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashNetworkApi {
    @GET("photos/")
    suspend fun getPhotos(@Query("client_id") clientId: String): List<NetworkPhoto>
}