package com.llinsoft.gptmobile.screens.chat_screen

import com.llinsoft.gptmobile.model.ApiModel
import com.llinsoft.gptmobile.model.ChatItem
import com.llinsoft.gptmobile.model.ChatTemperature
import com.llinsoft.gptmobile.model.PromptItem
import com.llinsoft.gptmobile.model.PromptType

data class ChatUiState(
    val prompt: PromptItem = PromptItem(
        0,
        PromptType.CHAT,
        "",
        ApiModel.GPT35,
        ChatTemperature.MED.temperature
    ),
    val chatMessage: String = "",
    val errorText: String = "",
    val chatHistory: List<ChatItem> = emptyList(),
)