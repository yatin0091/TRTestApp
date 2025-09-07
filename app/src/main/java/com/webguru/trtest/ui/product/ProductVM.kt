package com.webguru.trtest.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webguru.trtest.data.StockRepo
import com.webguru.trtest.data.model.RepoState
import com.webguru.trtest.data.model.StockOption
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductVM @Inject constructor(private val repo: StockRepo) : ViewModel() {
    // Optional: one-off event flow for snackbars when append fails
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events

    val uiState: StateFlow<StockUiState> =
        repo.state
            .onEach { rs ->
                // raise one-off events without changing structural UI state
                if (rs is RepoState.Data && rs.appendError != null) {
                    _events.tryEmit(rs.appendError)
                }
            }
            .map { rs ->
                when (rs) {
                    RepoState.InitialLoading ->
                        StockUiState.Loading

                    is RepoState.InitialError ->
                        StockUiState.Error(rs.message)

                    is RepoState.Data -> when {
                        rs.items.isEmpty() && !rs.isAppending ->
                            StockUiState.Empty

                        else ->
                            StockUiState.Content(
                                items = rs.items,
                                isAppending = rs.isAppending,
                                appendError = rs.appendError,
                                hasMore = rs.hasMore
                            )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StockUiState.Loading
            )

    // UI-driven intents (no work in init)
    fun refresh() = viewModelScope.launch { repo.refresh() }
    fun loadMore() = viewModelScope.launch { repo.loadMore() }
    fun retryAppend() = viewModelScope.launch { repo.retryAppend() }
}

sealed interface StockUiState {
    data object Loading : StockUiState
    data object Empty : StockUiState
    data class Content(
        val items: List<StockOption>,
        val isRefreshing: Boolean = false,   // optional if you add pull-to-refresh later
        val isAppending: Boolean = false,
        val appendError: String? = null,
        val hasMore: Boolean = true
    ) : StockUiState

    data class Error(val message: String) : StockUiState
}