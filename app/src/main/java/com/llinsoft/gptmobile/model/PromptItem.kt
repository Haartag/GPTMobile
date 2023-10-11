package com.llinsoft.gptmobile.model

import com.llinsoft.gptmobile.PromptEntity
import com.llinsoft.gptmobile.R

data class PromptItem(
    val id: Long,
    val type: PromptType,
    val prompt: String
)

fun PromptItem.toPromptEntity(): PromptEntity {
    return PromptEntity(this.id, this.type.name, this.prompt)
}

fun PromptEntity.toPromptItem(): PromptItem {
    return PromptItem(this.id, PromptType.valueOf(this.type), this.prompt)
}

enum class PromptType(
    val icon: Int
){
    TRANSLATE(
        icon = R.drawable.baseline_translate_24
    ),
    CODE(
        icon = R.drawable.baseline_code_24
    ),
    CHAT(
        icon = R.drawable.baseline_chat_24
    ),
    LETTER(
        icon = R.drawable.baseline_mail_24
    )
}
