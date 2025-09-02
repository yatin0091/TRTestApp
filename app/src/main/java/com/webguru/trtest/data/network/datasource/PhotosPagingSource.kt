package com.webguru.trtest.data.network.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.webguru.trtest.BuildConfig
import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.data.network.model.NetworkPhoto
import com.webguru.trtest.data.network.model.toPhoto
import com.webguru.trtest.data.network.retrofit.UnsplashNetworkApi
import javax.inject.Inject

class PhotosPagingSource @Inject constructor(
    private val api: UnsplashNetworkApi
) : PagingSource<Int, Photo>() {

    private val seenIds = mutableSetOf<String>() // per instance

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 1
            val perPage = params.loadSize
            val networkPhotos = api.getPhotos(page, perPage, BuildConfig.UNSPLASH_ACCESS_KEY)
            val data = networkPhotos.map { it.toPhoto() }

            // drop duplicates we've already emitted in this paging session
            val deduped = data.filter { seenIds.add(it.id) }

            LoadResult.Page(
                data = deduped,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (deduped.isEmpty()) null else page + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }
}