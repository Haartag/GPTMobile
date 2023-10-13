package com.llinsoft.gptmobile.screens.settings_screen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.llinsoft.gptmobile.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .padding(16.dp),
        ) {
            val uiState by viewModel.uiState.collectAsState()
            TokenInput(
                tokenState = uiState.tokenState,
                onTokenChange = { viewModel.updateToken(it) },
                onCheckClick = {
                    scope.launch {
                        var message = ""
                        viewModel.checkToken{ message = it}
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TokenInput(
    tokenState: TokenState,
    onTokenChange: (String) -> Unit,
    onCheckClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val controller = LocalSoftwareKeyboardController.current // keyboard controller to hide keyboard
        TextField(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            value = tokenState.token,
            onValueChange = {
                onTokenChange(it)
            },
            label = {
                Text(text = "OpenAI Token")
            },
            maxLines = 2,
            trailingIcon = {
                IconButton(
                    onClick = { // paste data from clipboard
                        val clipboardText =
                            clipboardManager.primaryClip?.getItemAt(0)?.text
                        clipboardText?.let { onTokenChange(it.toString()) }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_paste_24),
                        contentDescription = null
                    )
                }

            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { controller?.hide() }
            )
        )

        Text(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    controller?.hide()
                    onCheckClick()
                },
            color = tokenState.color,
            text = tokenState.text
        )
    }
}