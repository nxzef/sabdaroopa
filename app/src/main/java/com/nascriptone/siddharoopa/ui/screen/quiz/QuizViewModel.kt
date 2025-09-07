package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.lifecycle.ViewModel
import com.nascriptone.siddharoopa.data.model.Filter
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.uscs.SharedFavorites
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sharedFavorites: SharedFavorites
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizSectionState())
    val uiState: StateFlow<QuizSectionState> = _uiState.asStateFlow()

    fun updateSource(sourceWithData: SourceWithData) {
        _uiState.update { it.copy(sourceWithData = sourceWithData) }
    }

    fun updateMode(mode: Mode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun updateRange(range: Int) {
        _uiState.update { it.copy(range = range) }
    }

    fun updateFilter(filter: Filter) {
        _uiState.update { state ->
            when (val src = state.sourceWithData) {
                is SourceWithData.FromTable -> state.copy(
                    sourceWithData = src.copy(filter = filter)
                )

                else -> state
            }
        }
    }


}