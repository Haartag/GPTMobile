package com.llinsoft.gptmobile.model

enum class ChatTemperature(
    val index: Float,
    val temperature: Float,
    val topP: Float,
    val explanation: String
) {
    MIN(
        index = 0.0f,
        temperature = 0.2f,
        topP = 0.1f,
        explanation = "Very focused"
    ),
    LOW(
        index = 0.25f,
        temperature = 0.3f,
        topP = 0.2f,
        explanation = "Focused"
    ),
    MED(
        index = 0.5f,
        temperature = 0.5f,
        topP = 0.5f,
        explanation = "Medium"
    ),
    HI(
        index = 0.75f,
        temperature = 0.6f,
        topP = 0.7f,
        explanation = "Creative"
    ),
    TOP(
        index = 1.0f,
        temperature = 0.7f,
        topP = 0.8f,
        explanation = "Very creative"
    )
}

fun Float.getChatTemperature() = when (this) {
    0.0f -> ChatTemperature.MIN
    0.25f -> ChatTemperature.LOW
    0.5f -> ChatTemperature.MED
    0.75f -> ChatTemperature.HI
    1.0f -> ChatTemperature.TOP
    else -> ChatTemperature.MED
}