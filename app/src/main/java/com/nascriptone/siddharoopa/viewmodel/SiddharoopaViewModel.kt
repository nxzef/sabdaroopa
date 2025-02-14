package com.nascriptone.siddharoopa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.model.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AppUiState {
    data class Success(val data: List<Sabda>) : AppUiState()
    data class Error(val msg: String) : AppUiState()
    data object Loading : AppUiState()
}


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {


    private val _uiState = MutableStateFlow<AppUiState>(AppUiState.Loading)
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = AppUiState.Loading
            _uiState.value = try {
                AppUiState.Success(repository.getAllSabda())
            } catch (e: Exception) {
                AppUiState.Error(e.message.toString())
            }
        }
    }


}