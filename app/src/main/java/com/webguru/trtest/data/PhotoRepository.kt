package com.webguru.trtest.data

import android.util.Log
import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.data.network.PhotoNetworkDataSource
import com.webguru.trtest.data.network.model.toPhoto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
}

class PhotoRepositoryImpl @Inject constructor(
    private val photoNetworkDataSource: PhotoNetworkDataSource
) : PhotoRepository {
    override fun getPhotos(): Flow<List<Photo>> = flow {
        photoNetworkDataSource.getPhotos()
            .onSuccess { networkPhotos -> emit(networkPhotos.map { it.toPhoto() }) }
            .onFailure {
                Log.e("Error", "Error while fetching photos from network. ${it.message}")
                throw it
            }
    }.retryWhen { cause, attempt ->
        // Retry only for transient errors, with capped exponential backoff
        val isIo = cause is IOException
        val is5xx = (cause as? HttpException)?.code() in 500..599
        val shouldRetry = (isIo || is5xx) && attempt < 3
        if (shouldRetry) {
            val delayMs = 500L * (1 shl attempt.toInt()) // 500, 1000, 2000
            delay(delayMs)
        }
        shouldRetry
    }
}