package com.llinsoft.gptmobile.screens.settings_screen

import androidx.lifecycle.ViewModel
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesConstants.TOKEN_KEY
import com.llinsoft.gptmobile.data.local.datastore.EncryptedPreferencesHelper
import com.llinsoft.gptmobile.domain.ErrorToTextConverter
import com.llinsoft.gptmobile.domain.OpenAiManager
import com.llinsoft.gptmobile.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val openAiManager: OpenAiManager,
    private val errorToTextConverter: ErrorToTextConverter
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTokenFromPreferences()
    }

    fun updateToken(newToken: String) {
        _uiState.update {
            it.copy(
                tokenState = it.tokenState.copy(
                    token = newToken,
                    isTokenVerified = false,
                    isTokenValid = null
                )
            )
        }
    }

    private fun getTokenFromPreferences() {
        val token = encryptedPreferencesHelper.getPreference(TOKEN_KEY, "")
        if (token.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    tokenState = it.tokenState.copy(
                        token = token,
                        isTokenVerified = true,
                        isTokenValid = true
                    )
                )
            }
        }
    }

    // Save token to preferences only if it pass the check.
    private fun saveTokenToPreferences(newToken: String) {
        encryptedPreferencesHelper.putPreference(TOKEN_KEY, newToken)
    }

    /**
     * Check if the token is valid.
     */
    suspend fun checkToken(onCheck: (String) -> Unit) {
        val result = openAiManager.checkToken(_uiState.value.tokenState.token)
        when (result) {
            is Resource.Success -> {
                successfulTokenCheck()
                onCheck("Token updated successfully.")
            }
            is Resource.Error -> {
                errorTokenCheck()
                onCheck(errorToTextConverter.convertTokenException(result.exception))
            }
        }
    }

    private fun successfulTokenCheck() {
        _uiState.update {
            it.copy(
                tokenState = it.tokenState.copy(
                    isTokenValid = true,
                    isTokenVerified = true
                )
            )
        }
        saveTokenToPreferences(uiState.value.tokenState.token)
    }

    private fun errorTokenCheck() {
        _uiState.update {
            it.copy(
                tokenState = it.tokenState.copy(
                    isTokenValid = false,
                    isTokenVerified = true
                )
            )
        }
    }
}