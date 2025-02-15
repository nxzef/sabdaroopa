package com.nascriptone.siddharoopa.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            modifier = Modifier.padding(it)
        ) {
            composable(SiddharoopaScreen.Home.name) {
                Text("Hello this is home screen.!")
            }
            composable(SiddharoopaScreen.Search.name) {
                Text("Hello this is search screen.")
            }
        }
    }


}