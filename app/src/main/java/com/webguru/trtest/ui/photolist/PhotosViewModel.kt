package com.webguru.trtest.ui.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.webguru.trtest.data.PhotoRepository
import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.ui.trtesttype.TRTestTypeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotosViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _refreshing = MutableStateFlow(false)
    private val _appending = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // DB is SSOT → map Room flow to items and combine with transient flags
    val ui: StateFlow<UiState> =
        combine(
            photoRepository.getOfflineFirstPhotos()
                .onStart { emit(emptyList()) }, // show empty while DB emits first snapshot
            _refreshing,
            _appending,
            _error
        ) { items, refreshing, appending, err ->
            UiState(items = items, isRefreshing = refreshing, isAppending = appending, error = err)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState(isRefreshing = true) // initial top spinner until first DB emit/refresh
        )


    /** Append next page — keep list visible, just show a footer spinner */
    fun loadMore() {
        if (_appending.value || _refreshing.value) return
        _error.value = null
        _appending.value = true
        viewModelScope.launch {
            try {
                photoRepository.loadMore()// repo handles paging math + upsert
            } catch (e: Throwable) {
                _error.value = e.message
            } finally {
                _appending.value = false
            }
        }
    }

    /** Full refresh — keep list (if any) visible, show top spinner */
    fun manualRefresh() {
        if (_refreshing.value) return
        _error.value = null
        _refreshing.value = true
        viewModelScope.launch {
            try {
                photoRepository.refresh() // repo clears+seeds DB in a transaction
            } catch (e: Throwable) {
                _error.value = e.message
            } finally {
                _refreshing.value = false
            }
        }
    }
}

data class UiState(
    val items: List<Photo> = emptyList(),
    val isRefreshing: Boolean = false,
    val isAppending: Boolean = false,
    val error: String? = null
)

sealed interface PhotoUiState {
    object Loading : PhotoUiState
    data class Error(val errorMessage: String) : PhotoUiState
    data class Success(val photos: List<Photo>) : PhotoUiState
}
