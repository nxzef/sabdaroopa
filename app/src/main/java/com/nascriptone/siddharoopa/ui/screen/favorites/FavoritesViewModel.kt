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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    init {
        viewModelScope.launch {
            repository.getFavoriteIds().collect { totalIds ->
                _uiState.update { it.copy(totalIds = totalIds) }
                val dataSource = sharedDataDomain.sourceWithData.value
                if (dataSource !is SourceWithData.FromFavorites || totalIds.isEmpty()) return@collect
                enterSelectionMode(Trigger.INIT)
                _uiState.update { it.copy(selectedIds = dataSource.data) }
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

    fun transferDialogDismiss() {
        if (_uiState.value.transferState !is TransferState.Loading) {
            _uiState.update { it.copy(transferState = null) }
        }
    }

    fun onTakeQuizClick() {
        if (_uiState.value.transferState is TransferState.Loading) return
        val selectedIds = _uiState.value.selectedIds
        _uiState.update { it.copy(transferState = TransferState.Loading) }
        viewModelScope.launch {
            delay(2000)
            try {
                val sourceWithData = withContext(Dispatchers.IO) {
                    val words = repository.getWords(selectedIds)
                    val display = words.joinToString(", ")
                    SourceWithData.FromFavorites(
                        data = selectedIds, display = display
                    )
                }
                sharedDataDomain.updateSourceWithData(sourceWithData)
                _uiState.update { it.copy(transferState = TransferState.Success) }
            } catch (exception: Exception) {
                val errorMessage = exception.message ?: "An unexpected error occurred"
                _uiState.update {
                    it.copy(transferState = TransferState.Error(errorMessage))
                }
            }
        }
    }

    private fun enterSelectionMode(trigger: Trigger) {
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

    override fun onCleared() {
        super.onCleared()
        Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared")
        if (sharedDataDomain.sourceWithData.value is SourceWithData.FromFavorites) {
            Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared :::::::: Inside block")
        }
    }
}