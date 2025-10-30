package com.nxzef.sabdaroopa.domain.selection

import com.nxzef.sabdaroopa.utils.extensions.toggleInSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for common selection state operations
 */
@Singleton
class SelectionStateHelper @Inject constructor() {

    fun <T> toggleItemSelection(
        stateFlow: MutableStateFlow<T>,
        itemId: Int,
        getCurrentIds: T.() -> Set<Int>,
        updateIds: T.(Set<Int>) -> T
    ) {
        stateFlow.update { state ->
            val currentIds = state.getCurrentIds()
            val newIds = currentIds.toggleInSet(itemId)
            state.updateIds(newIds)
        }
    }

    fun <T> updateSelectedIds(
        stateFlow: MutableStateFlow<T>,
        newIds: Set<Int>,
        updateIds: T.(Set<Int>) -> T
    ) {
        stateFlow.update { it.updateIds(newIds) }
    }

}