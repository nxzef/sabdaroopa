package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.domain.ControllerUseCase
import com.nascriptone.siddharoopa.domain.SharedDataDomain
import com.nascriptone.siddharoopa.domain.SourceWithData
import com.nascriptone.siddharoopa.ui.state.DataTransState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataDomain: SharedDataDomain,
    private val controllerUseCase: ControllerUseCase
) : ViewModel() {

    val favorites: Flow<PagingData<Sabda>> =
        repository.getFavoriteList().flow.cachedIn(viewModelScope)
    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<DataTransState>()
    val uiEvent: SharedFlow<DataTransState> = _uiEvent.asSharedFlow()

    init {
        Log.d("MODE", "Triggered in the init block")
        startFavoritesCollection()
    }

    fun handleOnClose() {
        viewModelScope.launch {
            try {
                if (_uiState.value.trigger == Trigger.AUTO) {
                    _uiEvent.emit(DataTransState.Success)
                }
            } catch (e: Exception) {
                Log.e("EMIT", "UI Events Emit Failure", e)
            } finally {
                toggleSelectionMode()
            }
        }
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

//    private fun updateFavoriteSourceData(data: Set<Int>) {
//        _uiState.update { it.copy(dataTransState = DataTransState.Loading) }
//        viewModelScope.launch {
//            runCatching {
//                withContext(Dispatchers.IO) {
//                    delay(1000)
//                    val words = repository.getWords(data)
//                    val display = words.joinToString(", ")
//                    SourceWithData.FromFavorites(
//                        data = data,
//                        display = display
//                    )
//                }
//            }.onSuccess { sourceWithData ->
//                sharedDataDomain.updateSourceWithData(sourceWithData)
//                _uiState.update { it.copy(dataTransState = DataTransState.Success) }
//            }.onFailure {
//                _uiState.update { it.copy(dataTransState = DataTransState.Error) }
//            }
//        }
//    }

    private fun startFavoritesCollection() {
        viewModelScope.launch {
            Log.d("MODE", "Triggered in the init block on startFavoritesCollection")
            repository.getFavoriteIds().collect { totalIds ->
                _uiState.update { it.copy(totalIds = totalIds) }
                handleFavoritesSource()
            }
        }
    }

    private fun handleFavoritesSource() {
        val sourceData = sharedDataDomain.sourceWithData.value
        if (sourceData is SourceWithData.FromFavorites && _uiState.value.trigger == Trigger.NONE) {
            Log.d("MODE", "Triggered in the init block on handleFavoritesSource")
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
        controllerUseCase.enableFocus()
    }

    private fun exitSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectMode = false, selectedIds = emptySet(), trigger = Trigger.NONE
            )
        }
        controllerUseCase.disableFocus()
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