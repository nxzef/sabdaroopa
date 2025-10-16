package com.nascriptone.siddharoopa.ui.screen.home

import com.nascriptone.siddharoopa.ui.state.Filter
import com.nascriptone.siddharoopa.ui.state.TransferState
import com.nascriptone.siddharoopa.ui.state.Trigger

data class HomeState(
    val query: String = "",
    val filter: Filter = Filter(),
    val bottomSheetVisible: Boolean = false,
    val isSearchMode: Boolean = false,
    val isSelectMode: Boolean = false,
    val trigger: Trigger = Trigger.NONE,
    val selectedIds: Set<Int> = emptySet(),
    val transferState: TransferState? = null
)


