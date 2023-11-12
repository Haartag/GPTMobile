package com.llinsoft.gptmobile.screens.prompt_screen

import com.llinsoft.gptmobile.model.ApiModel
import com.llinsoft.gptmobile.model.PromptType

data class DialogUiState(
    val isDialogPromptDropdownExpanded: Boolean = false,
    val isDialogModelDropdownExpanded: Boolean = false,
    val dialogPrompt: String = "",
    val dialogPromptTypeSelected: PromptType = PromptType.TRANSLATE,
    val dialogModelSelected: ApiModel = ApiModel.GPT35,
    val promptText: String = ""
) {
    val promptTypes = PromptType.values()
    val models = ApiModel.values()
}