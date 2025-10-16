package com.nascriptone.siddharoopa.ui.screen.favorites

import com.nascriptone.siddharoopa.ui.state.TransferState
import com.nascriptone.siddharoopa.ui.state.Trigger

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val trigger: Trigger = Trigger.NONE,
    val selectedIds: Set<Int> = emptySet(),
    val totalIds: Set<Int> = emptySet(),
    val transferState: TransferState? = null
) {
    val areAllSelected: Boolean
        get() = selectedIds.isNotEmpty() && selectedIds.size == totalIds.size
}