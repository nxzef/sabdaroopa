package com.nascriptone.siddharoopa.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Navigation extensions for share ViewModel Instance.
 */
@Composable
inline fun <reified VM : ViewModel> NavController.sharedViewModelOrNull(
    gr: String
): VM? {
    val bse by currentBackStackEntryAsState()
    val vmo = remember( gr, bse) {
        runCatching { getBackStackEntry(gr) }.getOrNull()
    }
    return vmo?.let { hiltViewModel<VM>(it) }
}