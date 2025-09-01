package com.webguru.trtest.data

import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.data.network.PhotoNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PhotoRepository {
    suspend fun getPhotos(): Flow<List<Photo>>
}

class PhotoRepositoryImpl @Inject constructor(
    private val photoNetworkDataSource: PhotoNetworkDataSource
) : PhotoRepository {
    override suspend fun getPhotos(): Flow<List<Photo>> {
        TODO("Not yet implemented")
    }
}