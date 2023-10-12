package com.llinsoft.gptmobile.screens.prompt_screen

import com.llinsoft.gptmobile.model.PromptItem
import com.llinsoft.gptmobile.model.PromptType

data class PromptUiState(
    val selectedTab: Int = 0,
    val searchText: String = "",
    val promptCards: List<PromptItem> = emptyList(),
    val isDialogShown: Boolean = false,
) {
    val tabs = PromptType.values()
    private val selectedPromptType = tabs[selectedTab]
    val filteredPromptCards = promptCards.filter { it.type == selectedPromptType }
}