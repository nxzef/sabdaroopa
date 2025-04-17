package com.nascriptone.siddharoopa.ui.screen.favorites

import com.nascriptone.siddharoopa.data.model.uiobj.FavoriteSabdaDetails

data class FavoritesScreenState(val result: ScreenState = ScreenState.Loading)


sealed class ScreenState {
    data object Loading : ScreenState()
    data class Success(val data: List<FavoriteSabdaDetails>) : ScreenState()
    data class Error(val msg: String) : ScreenState()
}