package com.llinsoft.gptmobile.domain

import com.llinsoft.gptmobile.data.local.database.PromptDataSource
import com.llinsoft.gptmobile.utils.DefaultPrompts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Put default prompts from [DefaultPrompts] in database.
 */
class PrepopulateDatabase @Inject constructor(
    private val database: PromptDataSource
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun execute() {
        DefaultPrompts.defaults.forEach { prompt ->
            coroutineScope.launch {
                database.insertPrompt(prompt.id, prompt.type, prompt.prompt)
            }
        }
    }
}