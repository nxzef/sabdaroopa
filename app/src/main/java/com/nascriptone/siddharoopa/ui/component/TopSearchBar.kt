package com.nascriptone.siddharoopa.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nascriptone.siddharoopa.ui.screen.home.TextFieldData
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    viewModel: SiddharoopaViewModel,
    textFieldData: TextFieldData,
    modifier: Modifier = Modifier
) {

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .semantics { traversalIndex = 0f }
                .align(Alignment.TopCenter),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldData.text,
                    onQueryChange = {
                        viewModel.updateQuery(it)
                    },
                    onSearch = {
                        viewModel.updateTextFieldExpanded(false)
                    },
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
                                viewModel.updateTextFieldExpanded(false)
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
                                viewModel.updateTextFieldExpanded(true)
                            }) {
                                Icon(Icons.Default.Search, contentDescription = null)
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
                                viewModel.clearQuery()
                            }) {
                                Icon(Icons.Rounded.MoreVert, null)
                            }

                        }

                    },
                )
            },
            expanded = textFieldData.isSearchViewExpanded,
            onExpandedChange = { viewModel.updateTextFieldExpanded(it) }
        ) {

        }
    }
}


@Preview
@Composable
fun TopSearchBarPreview() {
    SiddharoopaTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopSearchBar(hiltViewModel(), TextFieldData())
            }
        }
    }
}