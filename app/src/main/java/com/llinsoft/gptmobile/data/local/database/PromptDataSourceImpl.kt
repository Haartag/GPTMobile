package com.llinsoft.gptmobile.data.local.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.llinsoft.gptmobile.PromptDatabase
import com.llinsoft.gptmobile.PromptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PromptDataSourceImpl(
    db: PromptDatabase
): PromptDataSource {

    private val queries = db.promptEntityQueries
    private val dispatcher = Dispatchers.IO

    override fun getAllPrompts(): Flow<List<PromptEntity>> {
        return queries.getAllPrompts().asFlow().mapToList(dispatcher)
    }

    override suspend fun getPromptById(id: Long): PromptEntity {
        return queries.getPromptById(id).executeAsOne()
    }

    override suspend fun deletePromptById(id: Long) {
        return withContext(dispatcher) {
            queries.deletePromptById(id)
        }
    }

    override suspend fun insertPrompt(id: Long?, type: String, prompt: String, model: String) {
        return withContext(dispatcher) {
            queries.insertPrompt(id, type, prompt, model)
        }
    }

    override suspend fun isDatabaseEmpty(): Boolean {
        return withContext(dispatcher) {
            queries.countPrompts().executeAsOneOrNull() == 0L
        }
    }

}