package com.nascriptone.siddharoopa.ui.screen.table

import com.nascriptone.siddharoopa.data.model.entity.Sabda

sealed interface FindState {
    data object Loading : FindState
    data class Success(val sabda: Sabda) : FindState
    data class Error(val message: String) : FindState
}
