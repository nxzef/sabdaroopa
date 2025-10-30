package com.nxzef.sabdaroopa.domain.quiz

import com.nxzef.sabdaroopa.ui.state.TransferState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages transfer state updates
 */
@Singleton
class TransferStateHandler @Inject constructor() {

    fun <T> setLoading(
        stateFlow: MutableStateFlow<T>,
        updateFn: T.(TransferState?) -> T
    ) {
        stateFlow.update { it.updateFn(TransferState.Loading) }
    }

    fun <T> setSuccess(
        stateFlow: MutableStateFlow<T>,
        updateFn: T.(TransferState?) -> T
    ) {
        stateFlow.update { it.updateFn(TransferState.Success) }
    }

    fun <T> setError(
        stateFlow: MutableStateFlow<T>,
        errorMessage: String,
        updateFn: T.(TransferState?) -> T
    ) {
        stateFlow.update { it.updateFn(TransferState.Error(errorMessage)) }
    }

    fun <T> dismiss(
        stateFlow: MutableStateFlow<T>,
        currentState: TransferState?,
        updateFn: T.(TransferState?) -> T
    ) {
        if (currentState !is TransferState.Loading) {
            stateFlow.update { it.updateFn(null) }
        }
    }

    fun canStartTransfer(currentState: TransferState?): Boolean {
        return currentState !is TransferState.Loading
    }
}