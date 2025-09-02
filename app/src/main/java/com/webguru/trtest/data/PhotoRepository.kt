package com.webguru.trtest.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.webguru.trtest.BuildConfig
import com.webguru.trtest.data.local.AppDatabase
import com.webguru.trtest.data.local.dao.PhotoDao
import com.webguru.trtest.data.local.model.PhotoEntity
import com.webguru.trtest.data.local.model.toPhoto
import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.data.network.datasource.PhotoNetworkDataSource
import com.webguru.trtest.data.network.datasource.PhotosPagingSource
import com.webguru.trtest.data.network.datasource.PhotosRemoteMediator
import com.webguru.trtest.data.network.model.toPhoto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>

    fun getOfflineFirstPhotos(): Flow<List<Photo>>

    suspend fun refresh()

    suspend fun loadMore()
}

class PhotoRepositoryImpl @Inject constructor(
    private val photoNetworkDataSource: PhotoNetworkDataSource,
    private val db: AppDatabase,
    private val photoDao: PhotoDao
) : PhotoRepository {
    override fun getPhotos(): Flow<List<Photo>> = flow {
        photoNetworkDataSource.getPhotos(1, 10)
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

    private val loadMutex = Mutex()
    private var nextPage = 1
    private val perPage = 10
    @Volatile private var endReached = false
    var retryAttempt = 0

    override fun getOfflineFirstPhotos(): Flow<List<Photo>> =
        photoDao.observeAll().onStart {
            // Seed DB only if empty; runs the first time the flow is collected.
            if (photoDao.count() == 0) {
                try { refresh() } catch (_: Throwable) { }
            }
        }.map { photoEntities -> photoEntities.map { it.toPhoto() } }

    override suspend fun refresh() = loadMutex.withLock {
        // fresh start
        nextPage = 1
        endReached = false
        db.withTransaction {
            photoDao.clearAll()
        }
        retryAttempt = 0
        loadNextPageLocked()
    }

    override suspend fun loadMore() = loadMutex.withLock {
        retryAttempt = 0
        loadNextPageLocked()
    }

    private suspend fun loadNextPageLocked() {
        if (endReached) return
        photoNetworkDataSource.getPhotos(
            page = nextPage,
            perPage = perPage
        ).onSuccess { dtos ->
            if (dtos.isEmpty()) {
                endReached = true
                return
            }

            db.withTransaction {
                val entities = dtos.mapIndexed { i, dto ->
                    PhotoEntity(
                        id = dto.id,
                        smallUrl = dto.urls.small,
                        fullUrl = dto.urls.full
                    )
                }
                photoDao.upsertAll(entities)
            }
            nextPage += 1
        }.onFailure {
            Log.e("Error", "Error while fetching photos from network. ${it.message}")
            val isIo = it is IOException
            val is5xx = (it as? HttpException)?.code() in 500..599
            val shouldRetry = (isIo || is5xx) && retryAttempt < 3
            if (shouldRetry) {
                val delayMs = 500L * (1 shl retryAttempt.toInt()) // 500, 1000, 2000
                delay(delayMs)
                retryAttempt++
                loadNextPageLocked()
            } else throw it
        }
    }

}