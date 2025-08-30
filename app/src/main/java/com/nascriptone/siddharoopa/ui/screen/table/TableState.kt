package com.nascriptone.siddharoopa.ui.screen.table

import com.nascriptone.siddharoopa.data.model.entity.Sabda

sealed interface FindState {
    data object Finding : FindState
    data class Found(val sabda: Sabda) : FindState
    data object NotFound : FindState
    data class Error(val message: String) : FindState
}
