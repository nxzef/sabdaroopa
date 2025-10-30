package com.nxzef.sabdaroopa.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusManager @Inject constructor() {
    private val _isFocused = MutableStateFlow(false)
    val isFocused: StateFlow<Boolean> = _isFocused.asStateFlow()

    fun enableFocus() = _isFocused.update { true }
    fun disableFocus() = _isFocused.update { false }
}