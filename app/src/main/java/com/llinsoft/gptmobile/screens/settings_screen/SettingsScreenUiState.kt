package com.llinsoft.gptmobile.screens.settings_screen

import androidx.compose.ui.graphics.Color


data class SettingsUiState(
    val tokenState: TokenState = TokenState()
)

data class TokenState(
    val token: String = "",
    val isTokenVerified: Boolean = false,
    val isTokenValid: Boolean? = null
) {
    val text = when {
        !isTokenVerified -> "Verify token"
        isTokenValid == true -> "Token verified"
        isTokenValid == false -> "Error: Wrong token!"
        else -> "Error: Unknown error."
    }
    val color = when {
        !isTokenVerified -> Color.Gray
        isTokenValid == true -> Color.Green
        else -> Color.Red
    }
}