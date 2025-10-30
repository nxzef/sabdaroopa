package com.nxzef.sabdaroopa.ui.screen.home

import com.nxzef.sabdaroopa.ui.state.Filter
import com.nxzef.sabdaroopa.ui.state.TransferState
import com.nxzef.sabdaroopa.ui.state.Trigger

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


