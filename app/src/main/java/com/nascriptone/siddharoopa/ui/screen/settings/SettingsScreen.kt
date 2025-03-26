package com.nascriptone.siddharoopa.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun SettingsScreen(
    settingsUIState: SettingsScreenState,
    viewModel: SiddharoopaViewModel,
    modifier: Modifier = Modifier
) {

    var isDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }


    Surface {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    isDialogOpen = true
                }
            ) {
                Text("Theme")
                Text(stringResource(settingsUIState.currentTheme.uiName))
            }
        }
        ThemeDialog(
            isDialogOpen = isDialogOpen,
            onDismissRequest = {
                isDialogOpen = false
            },
            onDone = {
                viewModel.updateTheme(it)
            },
            currentTheme = settingsUIState.currentTheme,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
        )
    }
}

@Composable
fun ThemeDialog(
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    onDone: (Theme) -> Unit,
    currentTheme: Theme,
    modifier: Modifier = Modifier,
) {

    val themes = Theme.entries
    var selectedTheme by rememberSaveable { mutableStateOf(currentTheme) }

    if (isDialogOpen) {
        Dialog(
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = modifier
                    .widthIn(min = 280.dp, max = 560.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Text(
                    stringResource(R.string.choose_theme),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                themes.forEach { theme ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clickable {
                                selectedTheme = theme
                            }
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = {
                                selectedTheme = theme
                            },
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(theme.uiName),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        onDone(selectedTheme)
                        onDismissRequest()
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}