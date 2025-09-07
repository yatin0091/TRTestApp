package com.webguru.trtest.data.model

sealed interface RepoState {
    data object InitialLoading : RepoState
    data class InitialError(val message: String) : RepoState

    // Emitted whenever the list changes or append status changes
    data class Data(
        val items: List<StockOption>,
        val hasMore: Boolean,
        val isAppending: Boolean = false,
        val appendError: String? = null
    ) : RepoState
}