package com.llinsoft.gptmobile.domain

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesConstants.TOKEN_KEY
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesHelper
import com.llinsoft.gptmobile.model.ApiModel
import com.llinsoft.gptmobile.model.ChatTemperature
import com.llinsoft.gptmobile.utils.Resource
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class OpenAiManager @Inject constructor(
    encryptedPreferencesHelper: EncryptedPreferencesHelper
) {

    private var apiKey: String = encryptedPreferencesHelper.getPreference(TOKEN_KEY, "")

    //done via Get() so that the api key can be changed after initialization.
    private val config
        get() = OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 120.seconds),
        )

    private val openAI
        get() = OpenAI(config)

    /**
     * Check if the token is valid: query for a list of models available for this token.
     */
    suspend fun checkToken(token: String): Resource<Boolean> {
        apiKey = token
        try {
            openAI.models()
        } catch (e: Exception) {
            return Resource.Error(e)
        }
        return Resource.Success(true)
    }

    /**
     * Request chat completion
     */
    suspend fun askForText(
        system: String,
        request: String,
        model: ApiModel,
        temperature: ChatTemperature
    ): Resource<ChatCompletion> {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model.model),
            temperature = temperature.temperature.toDouble(),
            topP = temperature.topP.toDouble(),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = system
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = request
                )
            )
        )
        return try {
            Resource.Success(openAI.chatCompletion(chatCompletionRequest))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}