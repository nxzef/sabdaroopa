package com.nascriptone.siddharoopa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.nascriptone.siddharoopa.ui.SiddharoopaApp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            SiddharoopaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SiddharoopaApp()
                }
            }
        }
    }
}