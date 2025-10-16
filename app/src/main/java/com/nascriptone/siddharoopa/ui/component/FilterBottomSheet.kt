package com.nascriptone.siddharoopa.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.ui.state.Filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    visible: Boolean,
    currentFilter: Filter,
    onDismissRequest: () -> Unit,
    onApplyFilter: (Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(true)
    var tempFilter by remember { mutableStateOf(currentFilter) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = sheetState, modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Filter Options", style = MaterialTheme.typography.titleLarge
                    )
                    if (tempFilter.isActive) TextButton(onClick = { tempFilter = Filter() }) {
                        Text("Reset")
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            // Category filter
            FilterSection(
                title = "Category",
                options = Category.entries,
                selectedOption = tempFilter.category,
                onOptionSelected = { tempFilter = tempFilter.copy(category = it) },
                onClear = {
                    tempFilter = tempFilter.copy(category = null)
                }) { category -> stringResource(category.skt) }

            // Sound filter
            FilterSection(
                title = "Sound",
                options = Sound.entries,
                selectedOption = tempFilter.sound,
                onOptionSelected = { tempFilter = tempFilter.copy(sound = it) },
                onClear = { tempFilter = tempFilter.copy(sound = null) }) { sound ->
                stringResource(
                    sound.skt
                )
            }

            // Gender filter
            FilterSection(
                title = "Gender",
                options = Gender.entries,
                selectedOption = tempFilter.gender,
                onOptionSelected = { tempFilter = tempFilter.copy(gender = it) },
                onClear = {
                    tempFilter = tempFilter.copy(gender = null)
                }) { gender -> stringResource(gender.skt) }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        scope.launch {
                            onApplyFilter(tempFilter)
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}


@Composable
fun <T> FilterSection(
    title: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    labelMapper: @Composable (T) -> String
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium
            )
            if (selectedOption != null) {
                TextButton(onClick = onClear) {
                    Text("Clear")
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                FilterChip(selected = option == selectedOption, onClick = {
                    onOptionSelected(if (option == selectedOption) null else option)
                }, label = { Text(labelMapper(option)) })
            }
        }
    }
}