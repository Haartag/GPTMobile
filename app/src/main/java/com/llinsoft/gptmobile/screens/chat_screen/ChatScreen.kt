package com.llinsoft.gptmobile.screens.chat_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.llinsoft.gptmobile.model.SenderType
import kotlinx.coroutines.delay
import java.util.Locale

// enable or disable chat when message sent??? what to do if user make 2 messages???
// error handling - chat or snackbar ???
// make loading card with sort of shimmer???
// Tertiary color for black theme - too light??? is tertiary color - secondary in another mode?
// remove top colored line (maybe only in dark mode)???


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    prompt: String,
    promptType: String,
    viewModel: ChatViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val configuration = LocalConfiguration.current
    val snackbarHostState = remember { SnackbarHostState() }
    val screenWidth = configuration.screenWidthDp
    val tileWidth = (screenWidth * 0.8f).dp
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.promptType)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { values ->
        LaunchedEffect(uiState.errorText) {
            if (uiState.errorText.isNotEmpty()) {
                snackbarHostState
                    .showSnackbar(
                        message = uiState.errorText,
                    )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(uiState.chatHistory) { chatItem ->
                    ChatTile(
                        sender = chatItem.senderType,
                        text = chatItem.text,
                        tileWidth = tileWidth,
                        inError = chatItem.inError
                    )
                }
                if (uiState.chatHistory.last().senderType == SenderType.USER) {
                    items(1) {
                        ChatTyping()
                    }
                }
            }
            ChatTextField(
                message = uiState.chatMessage,
                //enabled = uiState.errorText.isEmpty(),
                onSentClick = viewModel::sendMessage,
                onMessageChange = viewModel::updateText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTextField(
    message: String,
    //enabled: Boolean,
    onSentClick: () -> Unit,
    onMessageChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(32.dp)
                    ),
                value = message,
                onValueChange = { onMessageChange(it) },
                shape = RoundedCornerShape(32.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSentClick() }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        onSentClick()
                    }) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                    }
                },
                //enabled = enabled
            )
        }
    }
}

@Composable
fun ChatTile(
    sender: SenderType,
    text: String,
    tileWidth: Dp,
    inError: Boolean = false
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (sender == SenderType.USER) Alignment.TopEnd else Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 40f,
                        topEnd = 40f,
                        bottomStart = if (sender == SenderType.USER) 40f else 0f,
                        bottomEnd = if (sender == SenderType.USER) 0f else 40f,
                    )
                )
                .widthIn(16.dp, tileWidth)
                .background(
                    if (sender == SenderType.USER) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else MaterialTheme.colorScheme.primaryContainer
                )
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(if (sender == SenderType.USER) Alignment.End else Alignment.Start),
                    text = "${
                        sender.name.lowercase().replaceFirstChar { it.titlecase(Locale.ROOT) }
                    }:",
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = text,
                    color = if (inError) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun ChatTyping() {
    val textState = remember { mutableStateOf("Chat typing") }
    val typingStates = listOf(".", "..", "...")

    LaunchedEffect(key1 = true) {
        while (true) {
            typingStates.forEach { state ->
                textState.value = "Chat typing$state"
                delay(500) // Delay of 500ms
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = textState.value,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}