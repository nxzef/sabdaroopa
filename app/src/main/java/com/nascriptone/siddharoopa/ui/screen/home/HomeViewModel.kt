package com.nascriptone.siddharoopa.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.domain.ControllerUseCase
import com.nascriptone.siddharoopa.domain.SharedDataRepo
import com.nascriptone.siddharoopa.ui.state.Filter
import com.nascriptone.siddharoopa.ui.state.Trigger
import com.nascriptone.siddharoopa.utils.extensions.toggleInSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataRepo: SharedDataRepo,
    private val controllerUseCase: ControllerUseCase
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasAnyNonFavorite: StateFlow<Boolean> = _uiState
        .map { it.selectedIds }
        .distinctUntilChanged()
        .flatMapLatest { ids ->
            if (ids.isEmpty()) {
                flowOf(false)
            } else {
                repository.hasAnyNonFavoriteFlow(ids)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
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

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchMode = !it.isSearchMode
            )
        }
    }

    fun addSelectedItemsToFavorites() = addItemsToFavorite(_uiState.value.selectedIds)

    fun toggleSelectionMode(trigger: Trigger = Trigger.NONE) {
        if (_uiState.value.isSelectMode) exitSelectionMode()
        else enterSelectionMode(trigger = trigger)
    }

    fun toggleSelectedId(id: Int) {
        if (!_uiState.value.isSelectMode) enterSelectionMode(Trigger.CARD)
        val selectedIds = _uiState.value.selectedIds.toggleInSet(id)
        updateFavoriteSelectedSet(selectedIds)
        if (_uiState.value.selectedIds.isEmpty() && _uiState.value.trigger == Trigger.CARD) exitSelectionMode()
    }

    private fun enterSelectionMode(trigger: Trigger) {
        _uiState.update {
            it.copy(
                isSelectMode = true,
                trigger = trigger
            )
        }
        controllerUseCase.enableFocus()
    }

    private fun exitSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectMode = false,
                selectedIds = emptySet(),
                trigger = Trigger.NONE
            )
        }
        controllerUseCase.disableFocus()
    }

    private fun addItemsToFavorite(ids: Set<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val count = repository.addItemsToFavorite(ids, System.currentTimeMillis())
                val itm = if (count == 1) "item" else "items"
                _uiEvents.emit("$count $itm added to favorites")
            }.getOrElse {
                Log.d("ERROR", "Add items Error", it)
                _uiEvents.emit("Failed to add items to favorites")
            }
        }
    }

    private fun updateFavoriteSelectedSet(selectedIds: Set<Int>) =
        _uiState.update { it.copy(selectedIds = selectedIds) }

}