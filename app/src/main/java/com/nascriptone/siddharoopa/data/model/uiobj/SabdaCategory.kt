package com.nascriptone.siddharoopa.data.model.uiobj

import com.nascriptone.siddharoopa.ui.screen.TableCategory

data class CategoryOptionType(
    val sound: Sound,
    val displayWord: String
)

data class CategoryViewType(
    val title: String,
    val category: TableCategory,
    val options: List<CategoryOptionType>
)

data class Sound(
    val eng: String,
    val skt: String
)