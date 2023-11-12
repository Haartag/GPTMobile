package com.llinsoft.gptmobile.model

enum class ApiModel(
    val model: String,
    val label: String
) {
    GPT35(
        model = "gpt-3.5-turbo",
        label = "gpt 3.5"
    ),
    GPT4(
        model = "gpt-4",
        label = "gpt 4"
    )
}