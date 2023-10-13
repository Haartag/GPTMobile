package com.llinsoft.gptmobile.screens.prompt_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.llinsoft.gptmobile.Screen
import com.llinsoft.gptmobile.model.PromptItem
import com.llinsoft.gptmobile.model.PromptType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptScreen(
    viewModel: PromptViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
                    Text(text = "Prompts")
                },
                actions = {
                    IconButton(onClick = viewModel::openDialog) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "New prompt"
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.SettingsScreen.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { values ->
        if (uiState.isDialogShown) {
            NewPromptDialog(
                viewModel = viewModel,
                onDismiss = viewModel::closeDialog
            )
        }
        Column(
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
        ) {
            SortBar(
                tabs = uiState.tabs,
                selectedTab = uiState.selectedTab,
                onTabClick = viewModel::updateActiveTab,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredPromptCards) { promptItem ->
                        PromptTile(
                            promptItem = promptItem,
                            onDeleteClick = {
                                viewModel.deletePromptFromDb(it)
                                scope.launch {
                                    val result = snackbarHostState
                                        .showSnackbar(
                                            message = "Prompt deleted.",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Long
                                        )
                                    when (result) {
                                        SnackbarResult.ActionPerformed -> {
                                            viewModel.restoreDeletedPromptItem()
                                        }
                                        SnackbarResult.Dismissed -> {
                                            viewModel.cleanTemp()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SortBar(
    tabs: Array<PromptType>,
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TabRow(
            modifier = Modifier.height(60.dp),
            selectedTabIndex = selectedTab
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTab,
                    onClick = {
                        onTabClick(index)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.name,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromptTile(
    promptItem: PromptItem,
    onDeleteClick: (PromptItem) -> Unit
) {
    Card(
        modifier = Modifier
            .height(144.dp)
            .fillMaxWidth()
            .clickable {
                //TODO Chat navigation
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = promptItem.type.icon),
                    contentDescription = promptItem.type.name
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete prompt",
                    modifier = Modifier.clickable {
                        onDeleteClick(promptItem)
                    }

                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"${promptItem.prompt}\"",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewPromptDialog(
    viewModel: PromptViewModel,
    onDismiss: () -> Unit
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
                Box {
                    DropdownRow(
                        promptType = dialogUiState.dialogPromptTypeSelected,
                        modifier = Modifier.clickable { viewModel.openDropdownMenu() },
                        isSelected = true
                    )
                    DropdownMenu(
                        expanded = dialogUiState.isDialogDropdownExpanded,
                        onDismissRequest = { Unit }
                    ) {
                        dialogUiState.promptTypes.forEach { promptType ->
                            DropdownMenuItem(
                                text = {
                                    DropdownRow(promptType = promptType)
                                },
                                onClick = {
                                    viewModel.dropdownItemSelect(promptType)
                                }
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
private fun DropdownRow(
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
                contentDescription = promptType.name
            )
        }
    }
}