package com.llinsoft.gptmobile.screens.chat_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.llinsoft.gptmobile.data.local.database.PromptDataSource
import com.llinsoft.gptmobile.domain.ErrorToTextConverter
import com.llinsoft.gptmobile.domain.OpenAiManager
import com.llinsoft.gptmobile.model.ChatItem
import com.llinsoft.gptmobile.model.SenderType
import com.llinsoft.gptmobile.model.toPromptItem
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
    private val database: PromptDataSource,
    private val openAiManager: OpenAiManager,
    private val errorToTextConverter: ErrorToTextConverter,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private fun setPrompt() {
        viewModelScope.launch {
            try {
                val promptId: Long = checkNotNull(savedStateHandle["promptId"])
                val prompt = database.getPromptById(promptId)
                _uiState.update { uiState ->
                    uiState.copy(
                        prompt = prompt.toPromptItem(),
                        chatHistory = uiState.chatHistory + ChatItem(
                            text = prompt.prompt,
                            senderType = SenderType.SYSTEM
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { uiState ->
                    uiState.copy(
                        errorText = "Unknown Error: Something went wrong..."
                    )
                }
            }
        }
    }

    init {
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

    private fun getChatAnswer(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val response = openAiManager.askForText(
                system = _uiState.value.chatHistory.first().text,
                request = _uiState.value.chatHistory.last().text,
                model = _uiState.value.prompt.model
            )

            when (response) {
                is Resource.Success -> {
                    val result = response.data.choices[0].message.content
                    result?.let {
                        onSuccess(it)
                    }
                }
                is Resource.Error -> {
                    Log.d("ChatTag", "getChatAnswer: ", response.exception)
                    onError(errorToTextConverter.convertChatException(response.exception))
                }
            }
        }
    }
}