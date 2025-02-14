package com.nascriptone.siddharoopa.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.AppUiState
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun SiddharoopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                is AppUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is AppUiState.Error -> {
                    Text((uiState as AppUiState.Error).msg)
                }
                is AppUiState.Success -> {
                    (uiState as AppUiState.Success).data[0].word?.let { it1 -> Text(it1) }
                }
            }

        }
    }
}

@Preview
@Composable
fun SiddharoopaAppPreview() {
    SiddharoopaTheme {
        SiddharoopaApp()
    }
}