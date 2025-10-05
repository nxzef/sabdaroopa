package com.nascriptone.siddharoopa.ui.screen.table

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    val sabda: StateFlow<Sabda?> = repository.findSabdaById(id).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init { trackVisit() }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

    private fun trackVisit() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.trackVisit(id)
            }.getOrElse {
                Log.d("ERROR", "Track Visit Error", it)
            }
        }
    }

}