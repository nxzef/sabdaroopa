package com.nascriptone.siddharoopa.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

/**
 * Navigation extensions for ViewModel management
 */

@Composable
inline fun <reified VM : ViewModel> NavController.sharedViewModelOrNull(
    graphRoute: String
): VM? {
    val backStackEntry = remember(currentBackStackEntry, graphRoute) {
        try {
            getBackStackEntry(graphRoute)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
    return backStackEntry?.let { hiltViewModel<VM>(it) }
}
