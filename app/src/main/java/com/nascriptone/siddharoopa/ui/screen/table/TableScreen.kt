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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun TableScreen(
    viewModel: SiddharoopaViewModel, modifier: Modifier = Modifier
) {

    val tableUIState by viewModel.tableUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.parseStringToDeclension()
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

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetTableState()
        }
    }
}


@Composable
fun DeclensionTable(
    declensionTable: List<List<String>>, modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
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


