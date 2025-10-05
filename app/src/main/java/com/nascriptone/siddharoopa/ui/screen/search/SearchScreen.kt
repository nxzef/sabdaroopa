package com.nascriptone.siddharoopa.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchScreen(
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.ime.exclude(WindowInsets.navigationBars)
        )
    ) {
        if (searchResults.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (query.isBlank()) Icons.Rounded.History else Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (query.isBlank()) "No recent visits" else "No results found",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(state = rememberLazyListState()) {
                if (query.isBlank()) {
                    item {
                        Text(
                            text = "Recently Visited",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(items = searchResults, key = { it.id }) { sabda ->
                    ListItem(
                        headlineContent = {
                            Text("${sabda.word} \u2022 ${sabda.meaning}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }, supportingContent = {
                            Text(sabda.translit)
                        }, leadingContent = {
                            if (sabda.visitCount > 0) {
                                Icon(Icons.Rounded.History, "History")
                            } else {
                                Icon(Icons.Rounded.Search, "Search")
                            }
                        }, trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "ArrowForward",
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                            )
                        }, modifier = Modifier.clickable {
                            onItemClick(sabda.id)
                        })
                }
                item {
                    Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
                }
            }
        }
    }
}