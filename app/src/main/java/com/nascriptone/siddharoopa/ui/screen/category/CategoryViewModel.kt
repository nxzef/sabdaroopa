package com.nascriptone.siddharoopa.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.repository.AppRepository
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
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    repository: AppRepository,
) : ViewModel() {
    private val _filter = MutableStateFlow(Filter())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FilterState> = _filter.flatMapLatest { filter ->
        repository.getFilteredList(filter).map { data ->
            FilterState.Success(data)
        }.onStart {
            FilterState.Loading
        }.catch { e ->
            FilterState.Error(e.message ?: "Unknown error")
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FilterState.Loading
    )

    fun updateFilter(filter: Filter) = _filter.update { filter }
}