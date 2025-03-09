package com.nascriptone.siddharoopa.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.screen.home.TextFieldData
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    viewModel: SiddharoopaViewModel,
    textFieldData: TextFieldData,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }
    val searchViewToggleExpanded = !textFieldData.isSearchViewExpanded


    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldData.text,
                onQueryChange = { viewModel.updateQuery(it) },
                onSearch = { viewModel.updateTextFieldExpanded(searchViewToggleExpanded) },
                expanded = textFieldData.isSearchViewExpanded,
                onExpandedChange = { viewModel.updateTextFieldExpanded(it) },
                placeholder = { Text("Search a word") },
                leadingIcon = {

                    AnimatedVisibility(
                        textFieldData.isSearchViewExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            viewModel.updateTextFieldExpanded(searchViewToggleExpanded)
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }

                    AnimatedVisibility(
                        !textFieldData.isSearchViewExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            viewModel.updateTextFieldExpanded(searchViewToggleExpanded)
                        }) {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        }
                    }
                },
                trailingIcon = {

                    AnimatedVisibility(
                        textFieldData.isSearchViewExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            viewModel.clearQuery()
                        }) {
                            Icon(Icons.Rounded.Clear, null)
                        }
                    }

                    AnimatedVisibility(
                        !textFieldData.isSearchViewExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            expanded = !expanded
                        }) {
                            Icon(Icons.Rounded.MoreVert, null)
                        }

                    }

                },
            )
        },
        expanded = textFieldData.isSearchViewExpanded,
        onExpandedChange = { viewModel.updateTextFieldExpanded(it) },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            repeat(20) {
                Text("Hello World $it", modifier = Modifier.padding(16.dp))
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = !expanded
        }
    ) {
        DropdownMenuItem(
            text = { Text("Favorites") },
            onClick = { expanded = !expanded }
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { expanded = !expanded }
        )
    }

}
