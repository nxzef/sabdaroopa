package com.nascriptone.siddharoopa.ui.state

sealed interface DataTransState {
    data object None : DataTransState
    data object Loading : DataTransState
    data object Success : DataTransState
    data object Error : DataTransState
}