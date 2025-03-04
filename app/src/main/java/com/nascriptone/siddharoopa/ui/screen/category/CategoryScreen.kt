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
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
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
        if (!categoryScreenState.isDataFetched) {
            viewModel.fetchSabda()
        }
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
                sound = categoryScreenState.selectedSound,
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
    sound: Sound?,
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val filteredData = data.filter { sabda -> sabda.sound == sound?.eng }


    val tabItems: List<Sound> = listOf(
        Sound(
            eng = stringResource(R.string.vowel_eng),
            skt = stringResource(R.string.vowel_skt)
        ),
        Sound(
            eng = stringResource(R.string.consonant_eng),
            skt = stringResource(R.string.consonant_skt)
        )
    )

    Surface {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TabRow(
                selectedTabIndex = tabItems.indexOf(sound),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                tabItems.forEach { item ->
                    Tab(
                        selected = sound?.eng == item.eng,
                        onClick = {
                            viewModel.changeOption(item)
                        },
                        text = {
                            Text(item.skt, style = MaterialTheme.typography.titleLarge)
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = modifier
                    .weight(1F)
            ) {
                items(filteredData) { sabda ->
                    SabdaItem(
                        sabda = sabda,
                        onClick = {
                            viewModel.updateSelectedTable(sabda.declension, sabda.word)
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
        modifier = modifier
            .clickable(onClick = onClick)
    )
}


@Preview
@Composable
fun CategoryScreenPreview() {
    SiddharoopaTheme {
        CategoryScreenContent(listOf(), Sound("", ""), hiltViewModel(), rememberNavController())
    }
}