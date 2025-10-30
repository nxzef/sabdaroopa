package com.nxzef.sabdaroopa.ui.screen.quiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nxzef.sabdaroopa.R
import com.nxzef.sabdaroopa.ui.screen.Navigation
import com.nxzef.sabdaroopa.ui.screen.Routes
import com.nxzef.sabdaroopa.utils.extensions.sharedViewModelOrNull


val imageVector = Icons.AutoMirrored.Rounded.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val quizViewModel: QuizViewModel? =
        navHostController.sharedViewModelOrNull(Navigation.Quiz.name)
    if (quizViewModel == null) return

    val title = stringResource(R.string.start_your_quiz)
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = quizViewModel::onQuizHomeBack) {
                Icon(imageVector, null)
            }
        },
        actions = {
            IconButton(onClick = {
                navHostController.navigate(Routes.QuizInstruction.withRoot)
            }) {
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
    TopAppBar(
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
    TopAppBar(
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