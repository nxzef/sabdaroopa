package com.nascriptone.siddharoopa.data.model.uiobj

import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.screen.TableCategory

data class FavoriteSabdaDetails(
    val sabda: Sabda,
    val table: TableCategory,
)