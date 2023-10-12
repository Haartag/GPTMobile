package com.llinsoft.gptmobile.screens.prompt_screen

import com.llinsoft.gptmobile.model.PromptType

data class DialogUiState(
    val isDialogDropdownExpanded: Boolean = false,
    val dialogPrompt: String = "",
    val dialogPromptTypeSelected: PromptType = PromptType.TRANSLATE,
    val promptText: String = ""
) {
    val promptTypes = PromptType.values()
}