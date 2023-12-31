package com.llinsoft.gptmobile.data.local.database

import com.llinsoft.gptmobile.PromptEntity
import kotlinx.coroutines.flow.Flow

interface PromptDataSource {

    fun getAllPrompts(): Flow<List<PromptEntity>>

    suspend fun getPromptById(id: Long): PromptEntity

    suspend fun deletePromptById(id: Long)

    suspend fun insertPrompt(id: Long? = null, type: String, prompt: String, model: String, temperature: Double)

    suspend fun isDatabaseEmpty(): Boolean

}