package com.webguru.trtest.data.network

import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.data.model.safe
import com.webguru.trtest.data.network.model.NetworkPhoto
import com.webguru.trtest.data.network.retrofit.UnsplashNetworkApi
import retrofit2.HttpException
import javax.inject.Inject


interface PhotoNetworkDataSource {
    suspend fun getPhotos(): Result<List<NetworkPhoto>>
}

class PhotoNetworkDataSourceImpl @Inject constructor(
    private val unsplashNetworkApi: UnsplashNetworkApi
) : PhotoNetworkDataSource {
    override suspend fun getPhotos(): Result<List<NetworkPhoto>> =
        safe { unsplashNetworkApi.getPhotos() }
}