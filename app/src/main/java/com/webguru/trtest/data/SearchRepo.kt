package com.webguru.trtest.data

import com.webguru.trtest.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface SearchRepo {
    suspend fun local(q: String): Flow<List<Product>>
    suspend fun remote(q: String)
    suspend fun refresh(q: String)
}

class SearchRepoImpl @Inject constructor() : SearchRepo {
    override suspend fun local(q: String): Flow<List<Product>> {
        return flowOf(listOf(Product(id = 1, name = "foo")))
    }

    override suspend fun remote(q: String) {
    }

    override suspend fun refresh(q: String) {}
}