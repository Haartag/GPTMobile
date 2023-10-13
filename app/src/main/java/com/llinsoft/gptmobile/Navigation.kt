package com.llinsoft.gptmobile

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.llinsoft.gptmobile.screens.prompt_screen.PromptScreen
import com.llinsoft.gptmobile.screens.settings_screen.SettingsScreen

sealed class Screen(val route: String) {
    object PromptScreen : Screen("prompt")
    object SettingsScreen : Screen("settings")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.PromptScreen.route,
    ) {
        composable(Screen.PromptScreen.route) {
            PromptScreen(navController = navController)
        }
        composable(Screen.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
    }
}