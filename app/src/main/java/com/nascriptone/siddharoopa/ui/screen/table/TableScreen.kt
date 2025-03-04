package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun TableScreen(
    tableUIState: TableScreenState,
    viewModel: SiddharoopaViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    var isFetched by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        if (!isFetched) {
            viewModel.parseStringToDeclension()
            isFetched = true
        }
    }

    DisposableEffect(Unit) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.resetTableState()
                isFetched = false
            }
        }

        lifecycleOwner.value.lifecycle.removeObserver(observer)

        onDispose {
            lifecycleOwner.value.lifecycle.addObserver(observer)
        }

    }

    Surface(
        modifier = modifier
    ) {
        when (val result = tableUIState.result) {
            is StringParse.Loading -> CurrentState {
                CircularProgressIndicator()
            }

            is StringParse.Error -> CurrentState {
                Text(result.msg)
            }

            is StringParse.Success -> DeclensionTable(
                result.declensionTable
            )
        }
    }
}


@Composable
fun DeclensionTable(
    declensionTable: List<List<String>>, modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 24.dp)
                    .border(
                        BorderStroke(DividerDefaults.Thickness, DividerDefaults.color),
                        RoundedCornerShape(16.dp)
                    )
            ) {

                declensionTable.forEachIndexed { rowIndex, row ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {

                        row.forEachIndexed { cellIndex, cell ->

                            TableCell(
                                cell = cell,
                                cellIndex = cellIndex,
                                rowIndex = rowIndex,
                                row = row,
                                declensionTable = declensionTable,
                                modifier = Modifier
                                    .weight(1F)
                            )
                        }
                    }
                    if (rowIndex != declensionTable.lastIndex) {
                        HorizontalDivider()
                    }
                }

            }

        }
    }
}

@Composable
fun TableCell(
    cell: String,
    row: List<String>,
    rowIndex: Int,
    cellIndex: Int,
    declensionTable: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(
                setShape(
                    rowIndex,
                    cellIndex,
                    row.lastIndex,
                    declensionTable.lastIndex
                )
            )
            .background(
                color = separationValuesForHeader(
                    cellIndex,
                    rowIndex,
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    MaterialTheme.colorScheme.surfaceContainerLow
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            cell,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = separationValuesForHeader(
                cellIndex,
                rowIndex,
                FontWeight.Bold,
                LocalTextStyle.current.fontWeight
            )
        )
    }
    if (cellIndex != row.lastIndex) {
        VerticalDivider()
    }
}

fun <T> separationValuesForHeader(c: Int, r: Int, yes: T, no: T) =
    if (c == 0 || r == 0) yes else no

fun setShape(
    rowIndex: Int,
    cellIndex: Int,
    rowSize: Int,
    tableSize: Int
): Shape {
    return RoundedCornerShape(
        topStart = if (rowIndex == 0 && cellIndex == 0) 16.dp else 0.dp,
        topEnd = if (rowIndex == 0 && cellIndex == rowSize) 16.dp else 0.dp,
        bottomStart = if (rowIndex == tableSize && cellIndex == 0) 16.dp else 0.dp,
        bottomEnd = if (rowIndex == tableSize && cellIndex == rowSize) 16.dp else 0.dp
    )
}


