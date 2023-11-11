package com.llinsoft.gptmobile.model

data class ChatItem(
    val text: String,
    val senderType: SenderType,
    val inError: Boolean = false
)

enum class SenderType { SYSTEM, CHAT, USER }

