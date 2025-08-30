package com.nascriptone.siddharoopa.ui.screen.table

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val repository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["id"]) {
        "Missing id in SavedStateHandle"
    }

    val tableUIState: StateFlow<FindState> = repository.findSabdaById(id).map { sabda ->
        sabda?.let { FindState.Found(it) } ?: FindState.NotFound
    }.catch { e ->
        emit(FindState.Error(e.message ?: "Database error occurred"))
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FindState.Finding
    )

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

}