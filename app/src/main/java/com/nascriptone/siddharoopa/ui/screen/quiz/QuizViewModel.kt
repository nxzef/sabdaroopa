package com.nascriptone.siddharoopa.ui.screen.quiz

import android.util.Log
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

    fun updateSource(source: QuestionSource) {
        _uiState.update { it.copy(source = source) }
    }

    fun updateMode(mode: Mode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun updateRange(range: Int) {
        _uiState.update { it.copy(range = range) }
    }

    fun updateFilter(filter: Filter) {
        Log.d("TRIGGER", "FUN TRIGGERED $filter")
        _uiState.update {
//            it.copy(
//                data = Data.FromFilter(filter)
//            )
            if (it.data is Data.FromFilter) {
                it.copy(data = it.data.copy(filter = filter))
            } else it
        }
    }

}