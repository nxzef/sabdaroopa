package com.nxzef.sabdaroopa.ui.screen.favorites

import com.nxzef.sabdaroopa.ui.state.TransferState
import com.nxzef.sabdaroopa.ui.state.Trigger

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