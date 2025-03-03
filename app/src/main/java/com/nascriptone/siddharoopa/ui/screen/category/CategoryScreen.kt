package com.nascriptone.siddharoopa.ui.screen.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun CategoryScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
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
                viewModel = viewModel,
                navHostController = navHostController,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CategoryScreenContent(
    data: List<Sabda>,
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val antas: Set<String> = data.map { it.anta }.toSet()

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
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = modifier
            ) {
                items(data) { sabda ->
                    SabdaItem(
                        sabda = sabda,
                        onClick = {
                            viewModel.updateSelectedTable(sabda.declension)
                            navHostController.navigate(SiddharoopaRoutes.Table.name)
                        }
                    )
                }
            }

        }
    }

}

@Composable
fun SabdaItem(
    sabda: Sabda,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(sabda.word)
        },
        supportingContent = {
            Text(sabda.anta)
        },
        overlineContent = {
            Text(sabda.translit)
        },
        modifier = modifier
            .clickable(onClick = onClick)
    )
}


@Preview
@Composable
fun CategoryScreenPreview() {
    SiddharoopaTheme {
        CategoryScreenContent(listOf(), hiltViewModel(), rememberNavController())
    }
}