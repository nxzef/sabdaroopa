package com.nascriptone.siddharoopa.ui.screen.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.model.Filter
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.uscs.SharedDataDomain
import com.nascriptone.siddharoopa.uscs.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedDataDomain: SharedDataDomain,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizSectionState())
    val uiState: StateFlow<QuizSectionState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sharedDataDomain.sourceWithData.collect { sourceWithData ->
                _uiState.update {
                    it.copy(sourceWithData = sourceWithData)
                }
            }
        }
    }

    fun updateSource(source: Source) = sharedDataDomain.updateSourceWithData(source)

    fun updateMode(mode: Mode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun updateRange(range: Int) {
        _uiState.update { it.copy(range = range) }
    }

    fun updateFilter(filter: Filter) = sharedDataDomain.updateFilter(filter)

    override fun onCleared() {
        super.onCleared()
        sharedDataDomain.resetSource()
        Log.d("QuizViewModel", "SharedDataDomain state reset on ViewModel clear")
    }
}