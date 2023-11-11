package com.llinsoft.gptmobile.screens.chat_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.llinsoft.gptmobile.domain.ErrorToTextConverter
import com.llinsoft.gptmobile.domain.OpenAiManager
import com.llinsoft.gptmobile.model.ChatItem
import com.llinsoft.gptmobile.model.SenderType
import com.llinsoft.gptmobile.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val openAiManager: OpenAiManager,
    private val errorToTextConverter: ErrorToTextConverter,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var prompt: String

    /**
     * Get prompt type from navigation
     */
    private fun setPromptType() {
        try {
            val promptType: String = checkNotNull(savedStateHandle["promptType"])
            _uiState.update { uiState ->
                uiState.copy(
                    promptType = promptType.lowercase().replaceFirstChar { it.titlecaseChar() }
                )
            }
        } catch (e: IllegalStateException) { // Navigation error
            _uiState.update { uiState ->
                uiState.copy(
                    errorText = "Unknown Error: Something went wrong..."
                )
            }
        }
    }

    /**
     * Get prompt from navigation
     */
    private fun setPrompt() {
        try {
            prompt = checkNotNull(savedStateHandle["prompt"])
        } catch (e: IllegalStateException) { // Navigation error
            _uiState.update { uiState ->
                uiState.copy(
                    errorText = "Unknown Error: Something went wrong..."
                )
            }
        }
        _uiState.update {
            it.copy(
                chatHistory = it.chatHistory + ChatItem(
                    text = prompt,
                    senderType = SenderType.SYSTEM
                )
            )
        }
    }

    init {
        setPromptType()
        setPrompt()
    }

    fun updateText(newText: String) {
        _uiState.update {
            it.copy(chatMessage = newText)
        }
    }

    private fun updateError(error: String = "") {
        _uiState.update {
            it.copy(
                errorText = error
            )
        }
    }

    private fun addMessage(message: ChatItem) {
        _uiState.update {
            it.copy(
                chatHistory = it.chatHistory + message,
                chatMessage = if (message.senderType == SenderType.USER) {
                    ""
                } else {
                    _uiState.value.chatMessage
                }
            )
        }
    }


    fun sendMessage() {
        if (_uiState.value.chatMessage.isNotEmpty()) {
            addMessage(
                ChatItem(
                    text = _uiState.value.chatMessage,
                    senderType = SenderType.USER
                )
            )
            getChatAnswer(
                onSuccess = { result ->
                    addMessage(
                        ChatItem(
                            text = result,
                            senderType = SenderType.CHAT
                        )
                    )
                },
                onError = { result ->
                    updateError(result)
                    addMessage(
                        ChatItem(
                            text = result,
                            senderType = SenderType.SYSTEM,
                            inError = true
                        )
                    )
                }

            )
        }
    }

    @OptIn(BetaOpenAI::class)
    private fun getChatAnswer(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val response = openAiManager.askForText(prompt, _uiState.value.chatHistory.last().text)

            when (response) {
                is Resource.Success -> {
                    val result = response.data.choices[0].message.content
                    result?.let {
                        onSuccess(it)
                    }
                }

                is Resource.Error -> {
                    onError(errorToTextConverter.convertChatException(response.exception))
                }
            }


        }
    }
}