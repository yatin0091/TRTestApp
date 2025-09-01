/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webguru.trtest.ui.trtesttype

import androidx.compose.material3.NavigationRail
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.webguru.trtest.data.TRTestTypeRepository
import com.webguru.trtest.ui.trtesttype.TRTestTypeUiState.Error
import com.webguru.trtest.ui.trtesttype.TRTestTypeUiState.Loading
import com.webguru.trtest.ui.trtesttype.TRTestTypeUiState.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TRTestTypeViewModel @Inject constructor(
    private val tRTestTypeRepository: TRTestTypeRepository
) : ViewModel() {

    val uiState: StateFlow<TRTestTypeUiState> = tRTestTypeRepository
        .tRTestTypes.map<List<String>, TRTestTypeUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    // Use SharedFlow for one-time navigation events
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    fun addTRTestType(name: String) {
        viewModelScope.launch {
            tRTestTypeRepository.add(name)
        }
    }

    fun navigateToPhotos(){
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToPhotos)
        }
    }
}

sealed interface TRTestTypeUiState {
    object Loading : TRTestTypeUiState
    data class Error(val throwable: Throwable) : TRTestTypeUiState
    data class Success(val data: List<String>) : TRTestTypeUiState
}

sealed interface NavigationEvent{
    object NavigateToPhotos : NavigationEvent
}
