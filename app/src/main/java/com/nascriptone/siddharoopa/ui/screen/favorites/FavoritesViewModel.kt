package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.domain.SharedDataDomain
import com.nascriptone.siddharoopa.domain.SourceWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataDomain: SharedDataDomain,
) : ViewModel() {

    val favorites: Flow<PagingData<Sabda>> =
        repository.getFavoriteList().flow.cachedIn(viewModelScope)
    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFavoriteIds().collect { totalIds ->
                _uiState.update { it.copy(totalIds = totalIds) }
                handleFavoritesSource()
            }
        }
    }

    fun updateSourceWithData() {
        val data = _uiState.value.selectedIds
        val sourceWithData = SourceWithData.FromFavorites(data)
        sharedDataDomain.updateSourceWithData(sourceWithData)
    }

    fun toggleFavoriteSelectAll() = _uiState.update {
        it.copy(
            selectedIds = if (it.selectedIds.size < it.totalIds.size) it.totalIds
            else emptySet()
        )
    }

    fun toggleSelectedId(id: Int) {
        if (!_uiState.value.isSelectMode) enterSelectionMode(Trigger.CARD)
        updateFavoriteSelectedSet(id)
        if (_uiState.value.selectedIds.isEmpty() && _uiState.value.trigger == Trigger.CARD) exitSelectionMode()
    }

    fun deleteAllItemFromFavorite() = removeItemsFromFavorite(_uiState.value.selectedIds)

    fun toggleSelectionMode(trigger: Trigger = Trigger.NONE) {
        if (_uiState.value.isSelectMode) exitSelectionMode()
        else enterSelectionMode(trigger = trigger)
    }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

    private fun handleFavoritesSource() {
        val sourceData = sharedDataDomain.sourceWithData.value
        if (sourceData is SourceWithData.FromFavorites && _uiState.value.trigger == Trigger.NONE) {
            toggleSelectionMode(Trigger.AUTO)
            _uiState.update { it.copy(selectedIds = sourceData.data) }
        }
    }

    private fun enterSelectionMode(trigger: Trigger) {
        if (_uiState.value.totalIds.isEmpty()) return
        _uiState.update {
            it.copy(
                isSelectMode = true, trigger = trigger
            )
        }
    }

    private fun exitSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectMode = false, selectedIds = emptySet(), trigger = Trigger.NONE
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

    private fun updateFavoriteSelectedSet(id: Int) {
        _uiState.update { it.copy(selectedIds = it.selectedIds.toggleInSet(id)) }
    }

    private fun <T> Set<T>.toggleInSet(id: T): Set<T> = if (id in this) this - id else this + id
}