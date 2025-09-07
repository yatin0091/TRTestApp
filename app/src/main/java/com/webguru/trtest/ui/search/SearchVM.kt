package com.webguru.trtest.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webguru.trtest.data.model.Product
import com.webguru.trtest.domain.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchVm @Inject constructor(
    private val useCase: SearchUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val queries = savedStateHandle.getStateFlow("q", "")

    val ui: StateFlow<SearchUi> =
        useCase.execute(queries)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUi())

    fun onQueryChange(q: String) { savedStateHandle["q"] = q }

}

data class SearchUi(
    val query: String = "",
    val items: List<Product> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

