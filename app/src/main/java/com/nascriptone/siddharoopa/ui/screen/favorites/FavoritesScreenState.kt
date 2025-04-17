package com.nascriptone.siddharoopa.ui.screen.favorites

data class FavoritesScreenState(val result: ScreenState = ScreenState.Loading)



sealed class ScreenState {
    data object Loading : ScreenState()
    data class Success(val result: List<String>) : ScreenState()
    data class Error(val msg: String) : ScreenState()
}