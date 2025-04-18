package com.nascriptone.siddharoopa.data.model.uiobj

import com.nascriptone.siddharoopa.ui.screen.TableCategory

data class CategoryOptionType(
    val sound: SoundLang,
    val displayWord: String
)

data class CategoryViewType(
    val title: String,
    val category: TableCategory,
    val options: List<CategoryOptionType>
)

data class SoundLang(
    val eng: String,
    val skt: String
)