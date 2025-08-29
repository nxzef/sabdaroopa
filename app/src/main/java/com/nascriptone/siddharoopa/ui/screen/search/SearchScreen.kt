package com.nascriptone.siddharoopa.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.data.model.entity.Sabda

@Composable
fun SearchScreen(
    items: List<Sabda>,
    modifier: Modifier = Modifier
) {
    Surface {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = modifier
        ) {
            items(items) {
                ListItem(
                    headlineContent = {
                        Text("Hello Naseef $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    supportingContent = {
                        Text("Hello")
                    },
                    leadingContent = {
//                        if (it < 6) {
//                            Icon(Icons.Rounded.History, "History")
//                        } else {
//                            Icon(Icons.Rounded.Search, "Search")
//                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "ArrowForward",
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                        )
                    },
                    modifier = Modifier.clickable {

                    }
                )
            }
            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}