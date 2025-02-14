package com.nascriptone.siddharoopa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nascriptone.siddharoopa.ui.SiddharoopaApp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiddharoopaTheme {
                SiddharoopaApp()
            }
        }
    }
}