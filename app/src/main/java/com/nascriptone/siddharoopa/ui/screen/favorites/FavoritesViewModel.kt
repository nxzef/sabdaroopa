package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
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
//    private val sharedDataDomain: SharedDataDomain,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val favorites: Flow<PagingData<Sabda>> =
        repository.getFavoriteList().flow.cachedIn(viewModelScope)
    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()
    private val ids = repository.getFavoriteIds().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet()
    )
    private val fromQuiz: Boolean = savedStateHandle.get<Boolean>("fq") ?: false

    init {
        Log.d("FROM_QUIZ", "$fromQuiz")
        viewModelScope.launch {
            ids.collect { ids ->
                _uiState.update { it.copy(totalIDs = ids) }
            }
        }
    }

    fun toggleFavoriteSelectAll() = _uiState.update {
        it.copy(
            selectedIds = if (it.selectedIds.size < ids.value.size) ids.value
            else emptySet()
        )
    }

    fun toggleSelectedId(id: Int) {
        if (!_uiState.value.isSelectMode) enterSelectionMode(SelectionTrigger.CARD)
        updateFavoriteSelectedSet(id)
        if (_uiState.value.selectedIds.isEmpty() &&
            _uiState.value.selectionTrigger == SelectionTrigger.CARD
        ) exitSelectionMode()
    }

    fun deleteAllItemFromFavorite() = removeItemsFromFavorite(_uiState.value.selectedIds)

    fun toggleSelectionMode(
        selectionTrigger: SelectionTrigger = SelectionTrigger.EXPLICIT,
        saveData: Boolean = false
    ) {
        if (_uiState.value.isSelectMode) exitSelectionMode(saveData)
        else enterSelectionMode(trigger = selectionTrigger)
    }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

    private fun enterSelectionMode(trigger: SelectionTrigger) {
        if (ids.value.isEmpty()) {
            viewModelScope.launch { _uiEvents.emit("No favorites yet") }
            return
        }
        _uiState.update {
            it.copy(
                isSelectMode = true,
                selectionTrigger = trigger
            )
        }
    }

    private fun exitSelectionMode(saveData: Boolean = false) {
        if (!saveData) clearFavoriteSelectedSet()
        _uiState.update { it.copy(isSelectMode = false) }
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

    private fun updateFavoriteSelectedSet(id: Int) {
        _uiState.update { it.copy(selectedIds = it.selectedIds.toggleInSet(id)) }
    }

    private fun clearFavoriteSelectedSet() {
        _uiState.update { it.copy(selectedIds = emptySet()) }
    }

    private fun <T> Set<T>.toggleInSet(id: T): Set<T> =
        if (id in this) this - id else this + id
}