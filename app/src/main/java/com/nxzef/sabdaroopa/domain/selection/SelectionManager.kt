package com.nxzef.sabdaroopa.domain.selection

import com.nxzef.sabdaroopa.domain.manager.FocusManager
import com.nxzef.sabdaroopa.ui.state.Trigger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionManager @Inject constructor(
    private val focusManager: FocusManager
) {
    /**
     * Enters selection mode for any screen
     */
    fun <T> enterSelectionMode(
        stateFlow: MutableStateFlow<T>,
        trigger: Trigger,
        updateFn: T.(Boolean, Trigger) -> T
    ) {
        stateFlow.update { it.updateFn(true, trigger) }
        focusManager.enableFocus()
    }

    /**
     * Exits selection mode for any screen
     */
    fun <T> exitSelectionMode(
        stateFlow: MutableStateFlow<T>,
        updateFn: T.(Boolean, Set<Int>, Trigger) -> T
    ) {
        stateFlow.update { it.updateFn(false, emptySet(), Trigger.NONE) }
        focusManager.disableFocus()
    }

    /**
     * Toggles selection mode
     */
    fun <T> toggleSelectionMode(
        stateFlow: MutableStateFlow<T>,
        isCurrentlyInSelectMode: Boolean,
        trigger: Trigger,
        enterUpdateFn: T.(Boolean, Trigger) -> T,
        exitUpdateFn: T.(Boolean, Set<Int>, Trigger) -> T
    ) {
        if (isCurrentlyInSelectMode) {
            exitSelectionMode(stateFlow, exitUpdateFn)
        } else {
            enterSelectionMode(stateFlow, trigger, enterUpdateFn)
        }
    }
}