package com.webguru.trtest.data

import com.webguru.trtest.data.model.RepoState
import com.webguru.trtest.data.model.StockOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface StockRepo {
    val state: Flow<RepoState>
    suspend fun refresh()                 // triggers initial/full reload
    suspend fun loadMore()                // triggers next-page fetch (repo decides if hasMore)
    suspend fun retryAppend()             // retries the last failed append
}

class StockRepoImpl @Inject constructor(): StockRepo{
    override val state: Flow<RepoState>
        get() = TODO("Not yet implemented")

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun loadMore() {
        TODO("Not yet implemented")
    }

    override suspend fun retryAppend() {
        TODO("Not yet implemented")
    }

}
