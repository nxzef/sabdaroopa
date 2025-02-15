package com.nascriptone.siddharoopa.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaScreen
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel


@Composable
fun SiddharoopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier
    ) {

        NavHost(
            startDestination = SiddharoopaScreen.Home.name,
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),

            ) {
            composable(SiddharoopaScreen.Home.name) {
                Column {
                    Text("Hello this is home screen.!")
                    IconButton(onClick = {
                        navController.navigate(SiddharoopaScreen.Search.name)
                    }) {
                        Icon(Icons.Default.Search, null)
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.general_category),
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text("रामः")
                        }
                    }
                }
            }
            composable(SiddharoopaScreen.Search.name) {
                Column {
                    Text("Hello this is search screen.")
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            }
        }

    }


}