package com.nxzef.sabdaroopa.ui.screen.home

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
import com.nxzef.sabdaroopa.ui.state.Filter
import com.nxzef.sabdaroopa.ui.state.Trigger
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
    private val selectionManager: SelectionManager,
    private val selectionStateHelper: SelectionStateHelper,
    private val favoriteOperations: FavoriteOperations,
    private val quizDataPreparer: QuizDataPreparer,
    private val transferStateHandler: TransferStateHandler
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

    val hasSelectionChanged: StateFlow<Boolean> =
        _uiState.map { it.selectedIds }.distinctUntilChanged().map { selectedIds ->
            when (val dataSource = sharedDataRepo.dataSource.value) {
                is DataSource.CustomList -> dataSource.hasChanged(selectedIds)
                else -> false
            }
        }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val hasAnyNonFavorite: StateFlow<Boolean> = _uiState
        .map { it.selectedIds }
        .distinctUntilChanged()
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(false)
            else repository.hasAnyNonFavoriteFlow(ids)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        val dataSource = sharedDataRepo.dataSource.value
        if (dataSource is DataSource.CustomList) {
            selectionManager.enterSelectionMode(_uiState, Trigger.INIT) { isSelectMode, trigger ->
                copy(isSelectMode = isSelectMode, trigger = trigger)
            }
            selectionStateHelper.updateSelectedIds(_uiState, dataSource.ids) { ids ->
                copy(selectedIds = ids)
            }
        }
    }

    // Search & Filter
    fun onClearQuery() {
        _uiState.update { it.copy(query = "") }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onClearFilter() {
        _uiState.update { it.copy(filter = Filter()) }
    }

    fun updateFilter(filter: Filter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun toggleBottomSheet() {
        _uiState.update { it.copy(bottomSheetVisible = !it.bottomSheetVisible) }
    }

    fun toggleSearch() {
        _uiState.update { it.copy(isSearchMode = !it.isSearchMode) }
    }

    // Selection Management
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
        if (sourceWithData !is DataSource.CustomList) return
        selectionStateHelper.updateSelectedIds(_uiState, sourceWithData.ids) { ids ->
            copy(selectedIds = ids)
        }
    }

    // Favorites
    fun addSelectedItemsToFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteOperations.addToFavorites(_uiState.value.selectedIds)
                .onSuccess { count ->
                    val message = favoriteOperations.formatSuccessMessage(count, FavoriteOperation.ADD)
                    _uiEvents.emit(message)
                }
                .onFailure {
                    Log.d("ERROR", "Add items Error", it)
                    val message = favoriteOperations.getErrorMessage(FavoriteOperation.ADD)
                    _uiEvents.emit(message)
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

    fun onTakeQuizClick() {
        if (!transferStateHandler.canStartTransfer(_uiState.value.transferState)) return

        transferStateHandler.setLoading(_uiState) { state -> copy(transferState = state) }

        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedIds

            quizDataPreparer.prepareDataSource(selectedIds) { ids, display ->
                DataSource.CustomList(ids = ids, display = display)
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
}