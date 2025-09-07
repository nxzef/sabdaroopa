package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.uscs.SharedFavorites
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedFavorites: SharedFavorites
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()
    private val ids = repository.getFavoriteIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet()
    )

    val favorites: Flow<PagingData<Sabda>> =
        repository.getFavoriteList().flow.cachedIn(viewModelScope)


    init {
        viewModelScope.launch {
            sharedFavorites.selectedIds.collect { selectedIds ->
                _uiState.update {
                    val areAllSelected =
                        selectedIds.isNotEmpty() && selectedIds.size == ids.value.size
                    it.copy(
                        selectedIds = selectedIds,
                        areAllSelected = areAllSelected
                    )
                }
            }
        }
    }

    fun toggleSelectAll() = sharedFavorites.toggleSelectAll(ids.value)

    fun toggleSelectedId(id: Int) {
        if (!_uiState.value.isSelectMode) enterSelectionMode(SelectionTrigger.CARD)
        sharedFavorites.updateSelectedSet(id)
        if (_uiState.value.selectedIds.isEmpty() &&
            _uiState.value.selectionTrigger == SelectionTrigger.CARD
        ) exitSelectionMode()
    }

    fun deleteAllItemFromFavorite() = removeItemsFromFavorite(_uiState.value.selectedIds)

    fun toggleSelectionMode() {
        if (_uiState.value.isSelectMode) exitSelectionMode()
        else enterSelectionMode(SelectionTrigger.TOOLBAR)
    }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

    private fun enterSelectionMode(trigger: SelectionTrigger) {
        if (trigger == SelectionTrigger.TOOLBAR && ids.value.isEmpty()) {
            viewModelScope.launch {
                _uiEvents.emit("No favorites yet")
            }
            return
        }
        _uiState.update {
            it.copy(
                isSelectMode = true,
                selectionTrigger = trigger
            )
        }
    }

    private fun exitSelectionMode() {
        sharedFavorites.clearSelectedSet()
        _uiState.update {
            it.copy(
                isSelectMode = false,
                selectionTrigger = SelectionTrigger.NONE
            )
        }
    }

    private fun removeItemsFromFavorite(ids: Set<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.removeItemsFromFavorite(ids)
            }.getOrElse {
                Log.d("ERROR", "Remove Sabda Error", it)
            }
        }
    }
}