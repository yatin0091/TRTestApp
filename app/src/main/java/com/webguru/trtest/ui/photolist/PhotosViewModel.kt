package com.webguru.trtest.ui.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webguru.trtest.data.PhotoRepository
import com.webguru.trtest.data.model.Photo
import com.webguru.trtest.ui.trtesttype.TRTestTypeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotosViewModel @Inject constructor(
    photoRepository: PhotoRepository
) : ViewModel() {

    val refresh = MutableStateFlow(Unit)

    val photoUiState: StateFlow<PhotoUiState> = refresh.flatMapLatest {
        photoRepository.getPhotos()
            .map<List<Photo>, PhotoUiState> { photos -> PhotoUiState.Success(photos = photos) }
            .onStart { emit(PhotoUiState.Loading) }
            .catch { throwable ->
                emit(value = PhotoUiState.Error(errorMessage = throwable.message ?: ""))
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = PhotoUiState.Loading
    )

    fun refresh() {
        refresh.value = Unit
    }
}

sealed interface PhotoUiState {
    object Loading : PhotoUiState
    data class Error(val errorMessage: String) : PhotoUiState
    data class Success(val photos: List<Photo>) : PhotoUiState
}
