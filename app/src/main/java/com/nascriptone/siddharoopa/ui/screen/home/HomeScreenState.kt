package com.nascriptone.siddharoopa.ui.screen.home

import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table

data class HomeScreenState(
    val selectedTable: Table = Table.entries.first(),
    val selectedSound: Sound = Sound.entries.first()
)