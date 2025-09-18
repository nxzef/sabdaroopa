package com.nascriptone.siddharoopa.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ControllerUseCase @Inject constructor() {
    private val _isInFocused = MutableStateFlow(false)
    val isInFocused: StateFlow<Boolean> = _isInFocused.asStateFlow()

    fun enableFocus() = _isInFocused.update { true }
    fun disableFocus() = _isInFocused.update { false }
}