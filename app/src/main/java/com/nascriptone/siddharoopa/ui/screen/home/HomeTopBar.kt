package com.nascriptone.siddharoopa.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nascriptone.siddharoopa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Rounded.Menu, null)
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Rounded.Search, null)
            }
        },
        modifier = modifier
    )
}