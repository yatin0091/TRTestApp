package com.webguru.trtest.domain

import com.webguru.trtest.data.SearchRepo
import com.webguru.trtest.domain.di.DispatcherModule
import com.webguru.trtest.ui.search.SearchUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repo: SearchRepo,
    @DispatcherModule.IoDispatcher private val io: CoroutineDispatcher
) {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun execute(queries: Flow<String>): Flow<SearchUi> =
        queries
            .map { it.trim() }
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isBlank()) {
                    flowOf(SearchUi(query = q, items = emptyList()))
                } else {
                    flow {
                        emit(SearchUi(query = q, loading = true)) // loading state
                        try {
                            withContext(io) { repo.refresh(q) }    // attempt network
                            // stream DB updates after refresh
                            repo.local(q).collect { items ->
                                emit(SearchUi(query = q, items = items, loading = false))
                            }
                        } catch (e: Exception) {
                            // still stream cached DB, but with error
                            repo.local(q).collect { items ->
                                emit(SearchUi(query = q, items = items, loading = false, error = e.message))
                            }
                        }
                    }
                }
            }
}