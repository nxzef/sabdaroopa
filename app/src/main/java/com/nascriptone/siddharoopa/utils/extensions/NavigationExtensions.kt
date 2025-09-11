package com.nascriptone.siddharoopa.utils.extensions

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

/**
 * Navigation extensions for ViewModel management
 */
@Composable
inline fun <reified VM : ViewModel> NavController.getViewModel(): VM {
    val backStackEntry = checkNotNull(currentBackStackEntry) {
        "No current backstack entry found for NavController"
    }
    return hiltViewModel(backStackEntry)
}