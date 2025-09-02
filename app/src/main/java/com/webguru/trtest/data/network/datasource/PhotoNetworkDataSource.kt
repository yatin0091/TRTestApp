package com.webguru.trtest.data.network.datasource

import com.webguru.trtest.BuildConfig
import com.webguru.trtest.data.network.model.safe
import com.webguru.trtest.data.network.model.NetworkPhoto
import com.webguru.trtest.data.network.retrofit.UnsplashNetworkApi
import javax.inject.Inject


interface PhotoNetworkDataSource {
    suspend fun getPhotos(page: Int, perPage: Int): Result<List<NetworkPhoto>>
}

class PhotoNetworkDataSourceImpl @Inject constructor(
    private val unsplashNetworkApi: UnsplashNetworkApi
) : PhotoNetworkDataSource {
    override suspend fun getPhotos(page: Int, perPage: Int): Result<List<NetworkPhoto>> =
        safe {
            unsplashNetworkApi.getPhotos(
                page = page,
                perPage = perPage,
                clientId = BuildConfig.UNSPLASH_ACCESS_KEY
            )
        }
}