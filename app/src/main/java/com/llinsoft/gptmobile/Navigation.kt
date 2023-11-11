package com.llinsoft.gptmobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.llinsoft.gptmobile.screens.chat_screen.ChatScreen
import com.llinsoft.gptmobile.screens.prompt_screen.PromptScreen
import com.llinsoft.gptmobile.screens.settings_screen.SettingsScreen

sealed class Screen(val route: String) {
    object PromptScreen : Screen("prompt")
    object SettingsScreen : Screen("settings")
    object ChatScreen : Screen("chat")
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
        composable("${Screen.ChatScreen.route}/{prompt}/{promptType}", arguments = listOf(
            navArgument("prompt") {
                type = NavType.StringType
            },
            navArgument("promptType") {
                type = NavType.StringType
            }
        )) {
            val prompt = remember {
                it.arguments?.getString("prompt")
            }
            val promptType = remember {
                it.arguments?.getString("promptType")
            }
            ChatScreen(
                navController = navController,
                prompt = prompt ?: "",
                promptType = promptType ?: "",
            )
        }
    }
}