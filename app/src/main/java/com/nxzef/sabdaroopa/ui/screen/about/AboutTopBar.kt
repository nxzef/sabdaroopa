package com.nxzef.sabdaroopa.ui.screen.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutTopBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text("About")
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
        },
        modifier = modifier
    )
}