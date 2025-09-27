package com.nascriptone.siddharoopa.ui.screen.favorites

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

enum class Trigger {
    NONE, INIT, CARD, TOOLBAR
}

sealed class TransferState {
    data object Loading : TransferState()
    data object Success : TransferState()
    data class Error(val message: String) : TransferState()
}