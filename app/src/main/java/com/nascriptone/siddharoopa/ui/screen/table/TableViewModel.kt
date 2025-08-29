package com.nascriptone.siddharoopa.ui.screen.table

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _sabdaList = repository.getAllSabda().distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    private val _id = MutableStateFlow<Int?>(null)

    val tableUIState: StateFlow<FindState> = _sabdaList.map { sabdaList ->
        sabdaList.find { it.id == _id.value }?.let {
            FindState.Success(it)
        } ?: FindState.Error("Could not find declension table.")
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FindState.Loading
    )


    fun updateId(id: Int?) = _id.update { id }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }

}