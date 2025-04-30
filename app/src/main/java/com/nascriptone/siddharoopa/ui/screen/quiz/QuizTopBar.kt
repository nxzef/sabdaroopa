package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nascriptone.siddharoopa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.start_your_quiz)
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }
        },
        modifier = modifier
    )
}