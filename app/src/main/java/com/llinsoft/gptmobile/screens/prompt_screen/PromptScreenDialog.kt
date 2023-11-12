package com.llinsoft.gptmobile.screens.prompt_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.llinsoft.gptmobile.model.ApiModel
import com.llinsoft.gptmobile.model.PromptType
import com.llinsoft.gptmobile.model.getChatTemperature

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPromptDialog(
    viewModel: PromptViewModel,
    onDismiss: () -> Unit,
) {
    val dialogUiState by viewModel.dialogUiState.collectAsState()
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "New Prompt:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TemperatureSetupSlider(
                    sliderValue = dialogUiState.temperature,
                    sliderExplanation = dialogUiState.temperature.getChatTemperature().explanation,
                    updateSlider = viewModel::updateTemperatureSlider
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        PromptDropdownRow(
                            promptType = dialogUiState.dialogPromptTypeSelected,
                            modifier = Modifier.clickable { viewModel.openPromptDropdownMenu() },
                            isSelected = true
                        )
                        DropdownMenu(
                            expanded = dialogUiState.isDialogPromptDropdownExpanded,
                            onDismissRequest = { Unit }
                        ) {
                            dialogUiState.promptTypes.forEach { promptType ->
                                DropdownMenuItem(
                                    text = {
                                        PromptDropdownRow(promptType = promptType)
                                    },
                                    onClick = {
                                        viewModel.promptDropdownItemSelect(promptType)
                                    }
                                )
                            }
                        }
                    }
                    Box {
                        ModelDropdownRow(
                            model = dialogUiState.dialogModelSelected,
                            modifier = Modifier.clickable { viewModel.openModelDropdownMenu() },
                            isSelected = true
                        )
                        DropdownMenu(
                            expanded = dialogUiState.isDialogModelDropdownExpanded,
                            onDismissRequest = { Unit }
                        ) {
                            dialogUiState.models.forEach { model ->
                                DropdownMenuItem(
                                    text = {
                                        ModelDropdownRow(model = model)
                                    },
                                    onClick = {
                                        viewModel.modelDropdownItemSelect(model)
                                    }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(136.dp),
                    value = dialogUiState.promptText,
                    onValueChange = viewModel::updatePromptText,
                    label = {
                        Text(text = "Prompt:")
                    },
                    singleLine = false,
                    maxLines = 4,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(end = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.closeDialog() }
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = "Add",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.saveNewPrompt() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
private fun PromptDropdownRow(
    promptType: PromptType,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = promptType.icon),
            contentDescription = promptType.name
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = promptType.name.lowercase().replaceFirstChar { it.titlecaseChar() })
        if (isSelected) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "dropdown"
            )
        }
    }
}

@Composable
private fun ModelDropdownRow(
    model: ApiModel,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = model.label)
        if (isSelected) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "dropdown"
            )
        }
    }
}

@Composable
fun TemperatureSetupSlider(
    sliderValue: Float,
    sliderExplanation: String,
    updateSlider: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = sliderExplanation)
        Slider(
            value = sliderValue,
            onValueChange = updateSlider,
            steps = 3
        )
    }
}