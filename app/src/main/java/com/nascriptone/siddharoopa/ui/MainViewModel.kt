package com.nascriptone.siddharoopa.ui

import androidx.lifecycle.ViewModel
import com.nascriptone.siddharoopa.domain.ControllerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val controllerUseCase: ControllerUseCase
) : ViewModel()