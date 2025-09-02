package com.webguru.trtest.data.network.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.webguru.trtest.BuildConfig
import com.webguru.trtest.data.local.AppDatabase
import com.webguru.trtest.data.local.dao.PhotoDao
import com.webguru.trtest.data.local.dao.PhotoRemoteKeyDao
import com.webguru.trtest.data.local.model.PhotoEntity
import com.webguru.trtest.data.local.model.PhotoRemoteKey
import com.webguru.trtest.data.network.retrofit.UnsplashNetworkApi
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PhotosRemoteMediator @Inject constructor(
    private val api: UnsplashNetworkApi,
    private val db: AppDatabase,
    private val photoDao: PhotoDao,
    private val keyDao: PhotoRemoteKeyDao
) : RemoteMediator<Int, PhotoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult = try {
        // Determine page to load
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> {
                val first = state.firstItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
                keyDao.remoteKeyById(first.id)?.prevKey ?: return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val last = state.lastItemOrNull() ?: return MediatorResult.Success(true)
                keyDao.remoteKeyById(last.id)?.nextKey ?: return MediatorResult.Success(true)
            }
        }

        // Network fetch
        val perPage = state.config.pageSize
        val dtos = api.getPhotos(page = page, perPage = perPage, clientId = BuildConfig.UNSPLASH_ACCESS_KEY)

        val entities = dtos.map { dto ->
            PhotoEntity(
                id = dto.id,
                smallUrl = dto.urls.small,
                fullUrl = dto.urls.full
            )
        }

        // DB transaction: clear on refresh, upsert data + keys
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                keyDao.clearAll()
                photoDao.clearAll()
            }

            val endOfPaginationReached = entities.isEmpty()
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            photoDao.upsertAll(entities)
            keyDao.upsertAll(
                entities.map { PhotoRemoteKey(id = it.id, prevKey = prevKey, nextKey = nextKey) }
            )
        }

        MediatorResult.Success(endOfPaginationReached = entities.isEmpty())
    } catch (e: Throwable) {
        MediatorResult.Error(e)
    }
}