package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.CaseName
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.FormName
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import com.nascriptone.siddharoopa.utils.extensions.toPascalCase

@Composable
fun TableScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: TableViewModel = hiltViewModel()
) {
    val sabda by viewModel.sabda.collectAsStateWithLifecycle()

    sabda?.let {
        TableScreenContent(
            sabda = it,
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    } ?: EmptyTableState()
}

@Composable
private fun EmptyTableState() {
    CurrentState {
        Text("Could not find declension table.")
    }
}

@Composable
private fun TableScreenContent(
    sabda: Sabda,
    viewModel: TableViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.message.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            WordHeader(sabda)
            DeclensionTable(sabda.declension)

            Text(
                text = stringResource(R.string.details),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(12.dp))

            WordDetailsCard(sabda)
            Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight * 2))
        }
    }
}

@Composable
private fun WordHeader(sabda: Sabda) {
    Text(
        text = sabda.word,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Text(
        text = getSupportingText(sabda),
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun DeclensionTable(
    declension: Declension,
    modifier: Modifier = Modifier
) {
    val tableHeaders = remember {
        listOf(
            R.string.vibakti,
            R.string.single,
            R.string.dual,
            R.string.plural
        )
    }

    val caseNames = remember { CaseName.entries }

    OutlinedCard(modifier = modifier.padding(vertical = 24.dp)) {
        TableHeaderRow(headers = tableHeaders)
        HorizontalDivider()

        caseNames.forEachIndexed { index, caseName ->
            TableDataRow(
                caseName = caseName,
                forms = declension[caseName] ?: emptyMap()
            )
            if (index != caseNames.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun TableHeaderRow(headers: List<Int>) {
    Row(modifier = Modifier.height(54.dp)) {
        headers.forEachIndexed { index, headerRes ->
            TableCell(
                value = stringResource(headerRes),
                isHeader = true,
                modifier = Modifier.weight(1f)
            )
            if (index != headers.lastIndex) {
                VerticalDivider()
            }
        }
    }
}

@Composable
private fun TableDataRow(
    caseName: CaseName,
    forms: Map<FormName, String?>
) {
    Row(modifier = Modifier.height(54.dp)) {
        // Case name cell
        TableCell(
            value = stringResource(caseName.skt),
            isHeader = true,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider()

        // Form cells (SINGLE, DUAL, PLURAL)
        val formEntries = FormName.entries
        formEntries.forEachIndexed { index, formName ->
            TableCell(
                value = forms[formName],
                modifier = Modifier.weight(1f)
            )
            if (index != formEntries.lastIndex) {
                VerticalDivider()
            }
        }
    }
}

@Composable
private fun TableCell(
    value: String?,
    modifier: Modifier = Modifier,
    isHeader: Boolean = false,
) {
    val backgroundColor = if (isHeader) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val fontWeight = if (isHeader) {
        FontWeight.W700
    } else {
        LocalTextStyle.current.fontWeight
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        value?.let {
            Text(
                text = it,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
private fun WordDetailsCard(
    sabda: Sabda,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DetailRow(stringResource(R.string.word), sabda.word)
        HorizontalDivider()
        DetailRow(stringResource(R.string.meaning), sabda.meaning)
        HorizontalDivider()
        DetailRow(stringResource(R.string.roman_iast), sabda.translit)
        HorizontalDivider()
        DetailRow(stringResource(R.string.end_sound), sabda.anta)
        HorizontalDivider()
        DetailRow(
            stringResource(R.string.category),
            "${stringResource(sabda.category.skt)}\t-\t${sabda.category.toPascalCase()}"
        )
        HorizontalDivider()
        DetailRow(
            stringResource(R.string.gender),
            "${stringResource(sabda.gender.skt)}\t-\t${sabda.gender.toPascalCase()}"
        )
        HorizontalDivider()
        DetailRow(
            stringResource(R.string.sound),
            "${stringResource(sabda.sound.skt)}\t-\t${sabda.sound.toPascalCase()}"
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}