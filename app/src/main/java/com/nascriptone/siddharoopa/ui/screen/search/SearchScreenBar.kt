package com.nascriptone.siddharoopa.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.utils.extensions.sharedViewModelOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenBar(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val searchViewModel: SearchViewModel? =
        navHostController.sharedViewModelOrNull(Routes.Search.withRoot)
    if (searchViewModel == null) return
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Surface {
        TextField(
            value = query,
            onValueChange = searchViewModel::onQueryChange,
            placeholder = {
                Text("Search")
            },
            leadingIcon = {
                IconButton(onClick = {
                    navHostController.navigateUp()
                }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "ArrowBack")
                }
            },
            trailingIcon = {
                IconButton(onClick = searchViewModel::clearSearch) {
                    Icon(Icons.Rounded.Clear, "Clear")
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = DividerDefaults.color
            ),
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(TopAppBarDefaults.windowInsets.asPaddingValues())
                .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                .focusRequester(focusRequester)

        )
    }
}