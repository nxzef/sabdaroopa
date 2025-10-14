package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.domain.ControllerUseCase
import com.nascriptone.siddharoopa.domain.DataSource
import com.nascriptone.siddharoopa.domain.SharedDataRepo
import com.nascriptone.siddharoopa.ui.state.Trigger
import com.nascriptone.siddharoopa.utils.extensions.toggleInSet
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataRepo: SharedDataRepo,
    private val controllerUseCase: ControllerUseCase
) : ViewModel() {

    val favorites: Flow<PagingData<Sabda>> =
        repository.getFavoriteList().flow.cachedIn(viewModelScope)

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()
    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()

    val hasSelectionChanged: StateFlow<Boolean> =
        _uiState.map { it.selectedIds }.distinctUntilChanged().map { selectedIds ->
            when (val dataSource = sharedDataRepo.dataSource.value) {
                is DataSource.Favorites -> dataSource.hasChanged(selectedIds)
                else -> false
            }
        }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            repository.getFavoriteIds().collect { totalIds ->
                _uiState.update { it.copy(totalIds = totalIds) }
                val dataSource = sharedDataRepo.dataSource.value
                if (dataSource !is DataSource.Favorites || totalIds.isEmpty()) return@collect
                enterSelectionMode(Trigger.INIT)
                updateFavoriteSelectedSet(dataSource.ids)
            }
        }
    }

    fun toggleSelectAllFavorites() {
        val state = _uiState.value
        val selectedIds =
            if (state.selectedIds.size < state.totalIds.size) state.totalIds else emptySet()
        updateFavoriteSelectedSet(selectedIds)
    }

    fun toggleSelectedId(id: Int) {
        if (!_uiState.value.isSelectMode) enterSelectionMode(Trigger.CARD)
        val selectedIds = _uiState.value.selectedIds.toggleInSet(id)
        updateFavoriteSelectedSet(selectedIds)
        if (_uiState.value.selectedIds.isEmpty() && _uiState.value.trigger == Trigger.CARD) exitSelectionMode()
    }

    fun removeSelectedItemFromFavorite() = removeItemsFromFavorite(_uiState.value.selectedIds)

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

    fun onDiscardChanges() {
        val sourceWithData = sharedDataRepo.dataSource.value
        if (sourceWithData !is DataSource.Favorites) return
        updateFavoriteSelectedSet(sourceWithData.ids)
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
            try {
                val dataSource = withContext(Dispatchers.IO) {
                    val words = repository.getWords(selectedIds)
                    val display = words.joinToString(", ")
                    DataSource.Favorites(
                        ids = selectedIds,
                        display = display
                    )
                }
                sharedDataRepo.updateDataSource(dataSource)
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
                val count = repository.removeItemsFromFavorite(ids)
                val itm = if (count == 1) "item" else "items"
                _uiEvents.emit("$count $itm removed from favorites")
            }.getOrElse {
                Log.d("ERROR", "Remove Items Error", it)
                _uiEvents.emit("Failed to remove items from favorites")
            }
        }
    }

    private fun updateFavoriteSelectedSet(selectedIds: Set<Int>) =
        _uiState.update { it.copy(selectedIds = selectedIds) }


    override fun onCleared() {
        super.onCleared()
        Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared")
        if (sharedDataRepo.dataSource.value is DataSource.Favorites) {
            Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared :::::::: Inside block")
        }
    }
}