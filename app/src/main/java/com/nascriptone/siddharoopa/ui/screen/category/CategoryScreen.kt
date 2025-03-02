package com.nascriptone.siddharoopa.ui.screen.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun CategoryScreen(
    viewModel: SiddharoopaViewModel,
    categoryScreenState: CategoryScreenState,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.fetchSabda()
    }

    when (val result = categoryScreenState.result) {
        is DataFetchState.Loading -> {
            CurrentState {
                CircularProgressIndicator()
            }
        }

        is DataFetchState.Error -> {
            CurrentState {
                Text(result.msg)
            }
        }

        is DataFetchState.Success -> {
            CategoryScreenContent(
                result.data,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CategoryScreenContent(
    data: List<Sabda>,
    modifier: Modifier = Modifier
) {

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedSegmentIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabItems: List<String> = listOf(
        stringResource(R.string.vowel_skt),
        stringResource(R.string.consonant_skt)
    )
    val segmentItems: List<String> = listOf(
        "Masculine",
        "Feminine",
        "Neuter"
    )

    Surface {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                tabItems.forEachIndexed { index, label ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = {
                            Text(label, style = MaterialTheme.typography.titleLarge)
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    segmentItems.forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = index == selectedSegmentIndex,
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = segmentItems.size
                            ),
                            onClick = {
                                selectedSegmentIndex = index
                            }
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun CategoryScreenPreview() {
    SiddharoopaTheme {
        CategoryScreenContent(listOf())
    }
}