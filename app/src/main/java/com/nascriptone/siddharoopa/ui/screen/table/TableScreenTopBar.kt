package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nascriptone.siddharoopa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreenTopBar(
    title: String,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleTail = stringResource(R.string.sabda)
    val fullTitle = "$title $titleTail"
    CenterAlignedTopAppBar(
        title = {
            Text(fullTitle)
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.FavoriteBorder, null)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.MoreVert, null)
            }
        },
        modifier = modifier
    )
}