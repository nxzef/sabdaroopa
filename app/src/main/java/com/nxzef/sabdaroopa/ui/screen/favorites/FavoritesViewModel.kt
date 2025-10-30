package com.nxzef.sabdaroopa.ui.screen.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nxzef.sabdaroopa.data.model.entity.Sabda
import com.nxzef.sabdaroopa.data.repository.AppRepository
import com.nxzef.sabdaroopa.domain.DataSource
import com.nxzef.sabdaroopa.domain.SharedDataRepo
import com.nxzef.sabdaroopa.domain.favorites.FavoriteOperation
import com.nxzef.sabdaroopa.domain.favorites.FavoriteOperations
import com.nxzef.sabdaroopa.domain.quiz.QuizDataPreparer
import com.nxzef.sabdaroopa.domain.quiz.TransferStateHandler
import com.nxzef.sabdaroopa.domain.selection.SelectionManager
import com.nxzef.sabdaroopa.domain.selection.SelectionStateHelper
import com.nxzef.sabdaroopa.ui.state.Trigger
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
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataRepo: SharedDataRepo,
    private val selectionManager: SelectionManager,
    private val selectionStateHelper: SelectionStateHelper,
    private val favoriteOperations: FavoriteOperations,
    private val quizDataPreparer: QuizDataPreparer,
    private val transferStateHandler: TransferStateHandler
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

                selectionManager.enterSelectionMode(_uiState, Trigger.INIT) { isSelectMode, trigger ->
                    copy(isSelectMode = isSelectMode, trigger = trigger)
                }
                selectionStateHelper.updateSelectedIds(_uiState, dataSource.ids) { ids ->
                    copy(selectedIds = ids)
                }
            }
        }
    }

    // Selection Management
    fun toggleSelectAllFavorites() {
        val state = _uiState.value
        val selectedIds = if (state.selectedIds.size < state.totalIds.size) {
            state.totalIds
        } else {
            emptySet()
        }
        selectionStateHelper.updateSelectedIds(_uiState, selectedIds) { ids ->
            copy(selectedIds = ids)
        }
    }

    fun toggleSelectedId(id: Int) {
        val state = _uiState.value
        if (!state.isSelectMode) {
            selectionManager.enterSelectionMode(_uiState, Trigger.CARD) { isSelectMode, trigger ->
                copy(isSelectMode = isSelectMode, trigger = trigger)
            }
        }

        selectionStateHelper.toggleItemSelection(
            _uiState, id,
            getCurrentIds = { selectedIds },
            updateIds = { ids -> copy(selectedIds = ids) }
        )

        val updatedState = _uiState.value
        if (updatedState.selectedIds.isEmpty() && updatedState.trigger == Trigger.CARD) {
            selectionManager.exitSelectionMode(_uiState) { isSelectMode, ids, trigger ->
                copy(isSelectMode = isSelectMode, selectedIds = ids, trigger = trigger)
            }
        }
    }

    fun toggleSelectionMode(trigger: Trigger = Trigger.NONE) {
        val state = _uiState.value
        selectionManager.toggleSelectionMode(
            _uiState,
            state.isSelectMode,
            trigger,
            enterUpdateFn = { isSelectMode, trigger ->
                copy(isSelectMode = isSelectMode, trigger = trigger)
            },
            exitUpdateFn = { isSelectMode, ids, trigger ->
                copy(isSelectMode = isSelectMode, selectedIds = ids, trigger = trigger)
            }
        )
    }

    fun onDiscardChanges() {
        val sourceWithData = sharedDataRepo.dataSource.value
        if (sourceWithData !is DataSource.Favorites) return
        selectionStateHelper.updateSelectedIds(_uiState, sourceWithData.ids) { ids ->
            copy(selectedIds = ids)
        }
    }

    // Favorites Operations
    fun removeSelectedItemFromFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteOperations.removeFromFavorites(_uiState.value.selectedIds)
                .onSuccess { count ->
                    val message = favoriteOperations.formatSuccessMessage(count, FavoriteOperation.REMOVE)
                    _uiEvents.emit(message)
                }
                .onFailure {
                    Log.d("ERROR", "Remove Items Error", it)
                    val message = favoriteOperations.getErrorMessage(FavoriteOperation.REMOVE)
                    _uiEvents.emit(message)
                }
        }
    }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteOperations.toggleFavorite(id)
                .onSuccess { state ->
                    val message = if (state == 0) "Removed from favorites"
                    else "Added to favorites"
                    _uiEvents.emit(message)
                }
                .onFailure {
                    _uiEvents.emit(it.message ?: "Unknown Error")
                    Log.d("ERROR", "Toggle Sabda Error", it)
                }
        }
    }

    // Quiz Transfer
    fun transferDialogDismiss() {
        transferStateHandler.dismiss(
            _uiState,
            _uiState.value.transferState
        ) { state -> copy(transferState = state) }
    }

    fun onTakeQuizFromCard(id: Int) {
        selectionStateHelper.toggleItemSelection(
            _uiState, id,
            getCurrentIds = { selectedIds },
            updateIds = { ids -> copy(selectedIds = ids) }
        )
        onTakeQuizClick()
    }

    fun onTakeQuizClick() {
        if (!transferStateHandler.canStartTransfer(_uiState.value.transferState)) return

        transferStateHandler.setLoading(_uiState) { state -> copy(transferState = state) }

        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedIds

            quizDataPreparer.prepareDataSource(selectedIds) { ids, display ->
                DataSource.Favorites(ids = ids, display = display)
            }.onSuccess { dataSource ->
                sharedDataRepo.updateDataSource(dataSource)
                transferStateHandler.setSuccess(_uiState) { state -> copy(transferState = state) }
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "An unexpected error occurred"
                transferStateHandler.setError(_uiState, errorMessage) { state ->
                    copy(transferState = state)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared")
        if (sharedDataRepo.dataSource.value is DataSource.Favorites) {
            Log.d("VIEWMODEL_CLEAR", "Favorite ViewModel Cleared :::::::: Inside block")
        }
    }
}