package com.life.habitbloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.life.habitbloom.navigation.AppNavigation
import com.life.habitbloom.ui.theme.HabitBloomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // NEW FIX:
        // I removed enableEdgeToEdge() because the content was going too high
        // behind the phone status bar and camera area.
        setContent {
            HabitBloomTheme {
                AppNavigation()
            }
        }
    }
}