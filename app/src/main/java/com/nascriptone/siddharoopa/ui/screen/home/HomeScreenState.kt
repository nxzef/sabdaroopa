package com.nascriptone.siddharoopa.ui.screen.home

import com.nascriptone.siddharoopa.ui.screen.TableCategory

data class HomeScreenState(
    val selectedCategory: TableCategory = TableCategory.General,
    val categoryTitle: String = ""
)