package com.nascriptone.siddharoopa.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.ui.state.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val sabda: StateFlow<PagingData<Sabda>> =
        uiState.map { it.query to it.filter }.distinctUntilChanged().debounce(300)
            .flatMapLatest { (query, filter) ->
                repository.getSabdaWithFilters(
                    query = query.takeIf { it.isNotBlank() }, filter = filter
                )
            }.cachedIn(viewModelScope).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PagingData.empty()
            )

    fun onClearQuery() {
        _uiState.update {
            it.copy(
                query = ""
            )
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(
                query = query
            )
        }
    }

    fun onClearFilter() {
        _uiState.update {
            it.copy(
                filter = Filter()
            )
        }
    }

    fun updateFilter(filter: Filter) {
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
    }

    fun toggleBottomSheet() {
        _uiState.update {
            it.copy(
                bottomSheetVisible = !it.bottomSheetVisible
            )
        }
    }
}