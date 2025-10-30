package com.nxzef.sabdaroopa.ui.screen.table

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxzef.sabdaroopa.data.model.entity.Sabda
import com.nxzef.sabdaroopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val repository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()
    private val id: Int = checkNotNull(savedStateHandle["id"]) {
        "Missing id in SavedStateHandle"
    }

    private val fromSelectionMode: Boolean = savedStateHandle["sm"] ?: false

    val sabda: StateFlow<Sabda?> = repository.findSabdaById(id).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        if (!fromSelectionMode) trackVisit()
    }

    fun toggleFavoriteSabda() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val state = repository.toggleFavoriteAndGetState(id, System.currentTimeMillis())
                val message = if (state == 0) "Removed from favorites"
                else "Added to favorites"
                _message.emit(message)
            }.getOrElse {
                _message.emit(it.message ?: "Unknown Error")
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