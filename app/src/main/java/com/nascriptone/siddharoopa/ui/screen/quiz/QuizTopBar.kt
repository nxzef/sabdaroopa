package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nascriptone.siddharoopa.R


val imageVector = Icons.AutoMirrored.Rounded.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(
    onBackPress: () -> Unit,
    onInfoActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.start_your_quiz)
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(imageVector, null)
            }
        },
        actions = {
            IconButton(onClick = onInfoActionClick) {
                Icon(Icons.Outlined.Info, null)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreenTopBar(
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.quiz_result_title)
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizReviewScreenTopBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text("Review")
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(imageVector, null)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizInstructionScreenTopBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text("Quiz Instructions")
        },
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(imageVector, null)
            }
        },
        modifier = modifier
    )
}