package com.llinsoft.gptmobile.screens.chat_screen

import com.llinsoft.gptmobile.model.ChatItem

data class ChatUiState(
    val promptType: String = "Chat",
    val chatMessage: String = "",
    val errorText: String = "",
    val chatHistory: List<ChatItem> = emptyList()
)