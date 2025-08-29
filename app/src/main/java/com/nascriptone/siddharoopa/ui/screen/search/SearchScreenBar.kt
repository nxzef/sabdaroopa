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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var value by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
//    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Surface {
        TextField(
            value = value,
            onValueChange = { value = it },
            placeholder = {
                Text("Search")
            },
            leadingIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "ArrowBack")
                }
            },
            trailingIcon = {
                IconButton(onClick = { value = "" }) {
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

//Row(
//modifier = modifier
//.fillMaxWidth()
//.background(MaterialTheme.colorScheme.surfaceContainerHigh)
//.padding(TopAppBarDefaults.windowInsets.asPaddingValues())
//.height(TopAppBarDefaults.TopAppBarExpandedHeight),
//verticalAlignment = Alignment.CenterVertically,
//) {
//
//}