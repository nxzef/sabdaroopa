package com.nascriptone.siddharoopa.utils.extensions

import android.util.Log
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
        } catch (e: IllegalArgumentException) {
            Log.d("BACK_STACK", "No Destination Found", e)
            null
        }
    }
    return backStackEntry?.let { hiltViewModel<VM>(it) }
}
