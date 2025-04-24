package com.nascriptone.siddharoopa.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navHostController: NavHostController, modifier: Modifier = Modifier
) {

    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    fun navigate(route: SiddharoopaRoutes) {
        menuExpanded = !menuExpanded
        navHostController.navigate(route.name)
    }

    TopAppBar(
        title = {
        Text(stringResource(R.string.app_name))
    }, actions = {
        Box {
            IconButton(onClick = {
                menuExpanded = !menuExpanded
            }) {
                Icon(Icons.Rounded.MoreVert, null)
            }
            DropdownMenu(
                menuExpanded,
                onDismissRequest = {
                    menuExpanded = !menuExpanded
                },
                modifier = Modifier,
            ) {
                DropdownMenuItem(text = {
                    Text("Quiz")
                }, onClick = {
                    navigate(SiddharoopaRoutes.Quiz)
                })
                DropdownMenuItem(text = {
                    Text("Favorites")
                }, onClick = {
                    navigate(SiddharoopaRoutes.Favorites)
                })
                DropdownMenuItem(text = {
                    Text("Settings")
                }, onClick = {
                    navigate(SiddharoopaRoutes.Settings)
                })
            }
        }
    }, modifier = modifier
    )

}