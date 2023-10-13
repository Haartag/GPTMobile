package com.llinsoft.gptmobile.domain

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesConstants.TOKEN_KEY
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesHelper
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
        timeout = Timeout(socket = 60.seconds),
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
}