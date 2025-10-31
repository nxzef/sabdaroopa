package com.nxzef.sabdaroopa.ui.screen.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nxzef.sabdaroopa.data.model.LabelLanguage
import com.nxzef.sabdaroopa.data.model.TableFontSize
import com.nxzef.sabdaroopa.data.model.Theme
import com.nxzef.sabdaroopa.domain.platform.SystemCapabilities
import com.nxzef.sabdaroopa.ui.component.CurrentState
import com.nxzef.sabdaroopa.ui.component.DialogLayout
import com.nxzef.sabdaroopa.ui.component.StepSlider
import com.nxzef.sabdaroopa.ui.screen.quiz.Mode
import com.nxzef.sabdaroopa.utils.extensions.toPascalCase
import kotlinx.coroutines.launch


private enum class DialogData {
    None, TableFontSize, LabelLanguage, Theme, QuizMode
}

private data class DialogVisible(
    val visible: Boolean = false,
    val data: DialogData = DialogData.None
)

private val DialogVisibleSaver = Saver<DialogVisible, Map<String, Any>>(
    save = { dialogVisible ->
        mapOf(
            "visible" to dialogVisible.visible,
            "data" to dialogVisible.data.ordinal
        )
    },
    restore = { map ->
        DialogVisible(
            visible = map["visible"] as Boolean,
            data = DialogData.entries[map["data"] as Int]
        )
    }
)

@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    var dialogVisible by rememberSaveable(stateSaver = DialogVisibleSaver) {
        mutableStateOf(
            DialogVisible()
        )
    }

    Surface {
        if (state.isLoading) {
            CurrentState {
                CircularProgressIndicator()
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                GeneralSection(
                    currentTableFontSize = state.preferences.tableFontSize,
                    currentLabelLanguage = state.preferences.labelLanguage,
                    vibrationEnabled = state.preferences.isVibrationEnabled,
                    onTableFontSizeClick = {
                        dialogVisible = DialogVisible(
                            visible = true,
                            data = DialogData.TableFontSize
                        )
                    },
                    onLabelLanguageClick = {
                        dialogVisible = DialogVisible(
                            visible = true,
                            data = DialogData.LabelLanguage
                        )
                    },
                    onVibrationChanged = settingsViewModel::updateVibrationState,
                    modifier = Modifier.padding(top = 16.dp)
                )
                ThemeSection(
                    currentTheme = state.preferences.theme,
                    isDynamicColorEnabled = state.preferences.dynamicColorEnabled,
                    onThemeClick = {
                        dialogVisible = DialogVisible(
                            visible = true,
                            data = DialogData.Theme
                        )
                    },
                    onDynamicColorChanged = settingsViewModel::updateDynamicColor
                )
                QuizSection(
                    currentMode = state.preferences.defaultMode,
                    currentRange = state.preferences.defaultRange,
                    onModeClick = {
                        dialogVisible = DialogVisible(
                            visible = true,
                            data = DialogData.QuizMode
                        )
                    },
                    onRangeChanged = settingsViewModel::updateDefaultRange
                )
                AboutSection(onAboutClick = onAboutClick)
                Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
            }
            DynamicDialog(
                dialogVisible = dialogVisible,
                currentTableFontSize = state.preferences.tableFontSize,
                currentLabelLanguage = state.preferences.labelLanguage,
                currentTheme = state.preferences.theme,
                currentMode = state.preferences.defaultMode,
                onTableFontSizeChanged = settingsViewModel::updateTableFontSize,
                onLabelLanguageChanged = settingsViewModel::updateLabelLanguage,
                onThemeChanged = settingsViewModel::updateTheme,
                onModeChanged = settingsViewModel::updateDefaultMode,
                onDismissRequest = { dialogVisible = DialogVisible() }
            )
        }
    }
}

// General Section

@Composable
private fun GeneralSection(
    currentTableFontSize: TableFontSize,
    currentLabelLanguage: LabelLanguage,
    vibrationEnabled: Boolean,
    onTableFontSizeClick: () -> Unit,
    onLabelLanguageClick: () -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionLayout(
        title = "General", modifier = modifier
    ) {
        SectionDetails(
            headline = "Table Font Size",
            supportingText = currentTableFontSize.toPascalCase(),
            onClick = onTableFontSizeClick
        )
        HorizontalDivider()
        SectionDetails(
            headline = "Filter Label",
            supportingText = currentLabelLanguage.toPascalCase(),
            onClick = onLabelLanguageClick
        )
        HorizontalDivider()
        SectionDetails(
            headline = "Vibration",
            supportingText = "Provide tactile feedback when interacting with the app",
            action = {
                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = onVibrationChanged
                )
            }
        )
    }
}


// Theme Section

@Composable
private fun ThemeSection(
    currentTheme: Theme,
    isDynamicColorEnabled: Boolean,
    onThemeClick: () -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionLayout(
        title = "Theme", modifier = modifier
    ) {
        SectionDetails(
            headline = "App Theme",
            supportingText = currentTheme.toPascalCase(),
            onClick = onThemeClick
        )
        if (SystemCapabilities.supportsDynamicColors) {
            HorizontalDivider()
            SectionDetails(
                headline = "Dynamic Colors",
                supportingText = "Use colors from your wallpaper",
                action = {
                    Switch(
                        checked = isDynamicColorEnabled, onCheckedChange = onDynamicColorChanged
                    )
                })
        }
    }
}

// Quiz Section

@Composable
private fun QuizSection(
    currentMode: Mode,
    currentRange: Int,
    onModeClick: () -> Unit,
    onRangeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    SectionLayout(
        title = "Quiz",
        modifier = modifier
    ) {
        SectionDetails(
            headline = "Default Mode",
            supportingText = stringResource(currentMode.uiName),
            onClick = onModeClick
        )
        HorizontalDivider()
        SectionDetails(
            headline = "Default Range",
            onClick = { visible = true },
            action = {
                Text(
                    text = "$currentRange",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        )
    }
    QuizRangeBottomSheet(
        visible = visible,
        defaultRange = currentRange,
        onRangeChanged = onRangeChanged,
        onDismissRequest = { visible = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizRangeBottomSheet(
    visible: Boolean,
    defaultRange: Int,
    onRangeChanged: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var tempRange by rememberSaveable { mutableIntStateOf(defaultRange) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StepSlider(
                defaultRange = tempRange,
                onValueChangeFinished = { tempRange = it },
                label = "Default Range"
            )
            Spacer(Modifier.height(32.dp))
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
                    enabled = tempRange != defaultRange,
                    onClick = {
                        scope.launch {
                            onRangeChanged(tempRange)
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
            }
        }
    }
}

// About Section

@Composable
private fun AboutSection(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val (versionName, versionCode) = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val name = packageInfo.versionName ?: "Unknown"
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION") packageInfo.versionCode.toLong()
            }
            name to code
        } catch (_: Exception) {
            "Unknown" to 0L
        }
    }

    SectionLayout(
        title = "About", modifier = modifier
    ) {
        SectionDetails(
            headline = "About App",
            supportingText = "Version $versionName ($versionCode)",
            onClick = onAboutClick,
            action = {
                Icon(Icons.Rounded.ChevronRight, null)
            })
    }
}


@Composable
private fun SectionLayout(
    title: String, modifier: Modifier = Modifier, content: @Composable (ColumnScope.() -> Unit)
) {
    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.large
                )
                .clip(MaterialTheme.shapes.large), content = content
        )
    }
}

@Composable
private fun SectionDetails(
    headline: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    onClick: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(
                enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        ) {
            Text(text = headline, fontWeight = FontWeight.Medium)
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        action?.let {
            it.invoke()
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun DynamicDialog(
    dialogVisible: DialogVisible,
    currentTableFontSize: TableFontSize,
    currentLabelLanguage: LabelLanguage,
    currentTheme: Theme,
    currentMode: Mode,
    onTableFontSizeChanged: (TableFontSize) -> Unit,
    onLabelLanguageChanged: (LabelLanguage) -> Unit,
    onThemeChanged: (Theme) -> Unit,
    onModeChanged: (Mode) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!dialogVisible.visible) return

    when (dialogVisible.data) {
        DialogData.TableFontSize -> {
            EnumSelectionDialog(
                title = "Choose Font Size",
                options = TableFontSize.entries,
                currentOption = currentTableFontSize,
                onOptionSelected = onTableFontSizeChanged,
                onDismissRequest = onDismissRequest,
                modifier = modifier
            )
        }

        DialogData.LabelLanguage -> {
            EnumSelectionDialog(
                title = "Choose Language",
                options = LabelLanguage.entries,
                currentOption = currentLabelLanguage,
                onOptionSelected = onLabelLanguageChanged,
                onDismissRequest = onDismissRequest,
                modifier = modifier
            )
        }

        DialogData.Theme -> {
            EnumSelectionDialog(
                title = "Choose Theme",
                options = Theme.getAvailableThemes(),
                currentOption = currentTheme,
                onOptionSelected = onThemeChanged,
                onDismissRequest = onDismissRequest,
                modifier = modifier
            )
        }

        DialogData.QuizMode -> {
            EnumSelectionDialog(
                title = "Choose Mode",
                options = Mode.entries,
                currentOption = currentMode,
                onOptionSelected = onModeChanged,
                onDismissRequest = onDismissRequest,
                modifier = modifier
            )
        }

        DialogData.None -> return
    }
}

@Composable
private fun <T : Enum<T>> EnumSelectionDialog(
    title: String,
    options: List<T>,
    currentOption: T,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOption by rememberSaveable(
        stateSaver = Saver(save = { it.ordinal }, restore = { ordinal -> options[ordinal] })
    ) {
        mutableStateOf(currentOption)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        DialogLayout(
            modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
        ) {
            Column(modifier = modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                )

                HorizontalDivider()

                options.forEach { option ->
                    ListItem(
                        headlineContent = {
                            Text(text = option.toPascalCase())
                        }, leadingContent = {
                            RadioButton(
                                selected = option == selectedOption,
                                onClick = null,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }, colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ), modifier = Modifier.clickable {
                            selectedOption = option
                        })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.End)
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onOptionSelected(selectedOption)
                            onDismissRequest()
                        }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

