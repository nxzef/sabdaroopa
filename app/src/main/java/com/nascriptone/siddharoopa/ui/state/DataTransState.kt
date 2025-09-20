package com.nascriptone.siddharoopa.ui.state

import kotlinx.serialization.Serializable

@Serializable
sealed interface DataTransState {
    data object None : DataTransState
    data object Loading : DataTransState
    data object Success : DataTransState
    data class Error(val message: String) : DataTransState
}