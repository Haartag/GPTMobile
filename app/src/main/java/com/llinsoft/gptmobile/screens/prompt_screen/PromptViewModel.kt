package com.llinsoft.gptmobile.screens.prompt_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.llinsoft.gptmobile.data.local.database.PromptDataSource
import com.llinsoft.gptmobile.data.local.datastore.PreferencesDataStoreConstants.FIRST_LAUNCH_KEY
import com.llinsoft.gptmobile.data.local.datastore.PreferencesDataStoreHelper
import com.llinsoft.gptmobile.domain.PrepopulateDatabase
import com.llinsoft.gptmobile.model.ApiModel
import com.llinsoft.gptmobile.model.PromptItem
import com.llinsoft.gptmobile.model.PromptType
import com.llinsoft.gptmobile.model.toPromptItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(
    private val database: PromptDataSource,
    private val prepopulateDatabase: PrepopulateDatabase,
    private val preferencesDataStoreHelper: PreferencesDataStoreHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<PromptUiState>(PromptUiState())
    val uiState = _uiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow<DialogUiState>(DialogUiState())
    val dialogUiState = _dialogUiState.asStateFlow()

    private var deletedPromptItem: PromptItem? = null

    init {
        viewModelScope.launch {
            prepopulateOnFirstLaunch()
            getAllPrompts()
        }
    }

    fun updateActiveTab(newIndex: Int) {
        _uiState.update {
            it.copy(
                selectedTab = newIndex
            )
        }
    }

    fun openDialog() {
        _uiState.update {
            it.copy(
                isDialogShown = true
            )
        }
    }
    fun closeDialog() {
        _uiState.update {
            it.copy(
                isDialogShown = false
            )
        }
        _dialogUiState.update { // Reset dialog state to defaults
            DialogUiState()
        }
    }

    fun openPromptDropdownMenu() {
        _dialogUiState.update {
            it.copy(
                isDialogPromptDropdownExpanded = true
            )
        }
    }

    fun openModelDropdownMenu() {
        _dialogUiState.update {
            it.copy(
                isDialogModelDropdownExpanded = true
            )
        }
    }

    private fun closeDropdownMenu() {
        _dialogUiState.update {
            it.copy(
                isDialogPromptDropdownExpanded = false,
                isDialogModelDropdownExpanded = false
            )
        }
    }

    fun promptDropdownItemSelect(promptType: PromptType) {
        _dialogUiState.update {
            it.copy(
                dialogPromptTypeSelected = promptType
            )
        }
        closeDropdownMenu()
    }

    fun modelDropdownItemSelect(model: ApiModel) {
        _dialogUiState.update {
            it.copy(
                dialogModelSelected = model
            )
        }
        closeDropdownMenu()
    }

    fun updatePromptText(text: String) {
        _dialogUiState.update {
            it.copy(
                promptText = text
            )
        }
    }

    /**
     * Save deleted item to restore it via snackbar "Undo"
     */
    private fun saveDeletedToTemp(promptItem: PromptItem) {
        deletedPromptItem = promptItem
    }

    fun cleanTemp() {
        deletedPromptItem = null
    }

    /**
     * Restore deleted item on snackbar "Undo" click.
     */
    fun restoreDeletedPromptItem() {
        viewModelScope.launch {
            deletedPromptItem?.let {
                database.insertPrompt(
                    it.id,
                    it.type.name,
                    it.prompt,
                    it.model.model
                )
            }
            cleanTemp()
        }
    }


    /**
     * Collect prompts flow from database and map PromptEntity -> PromptItem
     */
    private suspend fun getAllPrompts() {
        database.getAllPrompts()
            .collect { promptEntities ->
                _uiState.update { state ->
                    state.copy(
                        promptCards = promptEntities.map { it.toPromptItem() }
                    )
                }
            }
    }

    fun saveNewPrompt() {
        viewModelScope.launch {
            database.insertPrompt(
                null,
                dialogUiState.value.dialogPromptTypeSelected.name,
                dialogUiState.value.promptText,
                dialogUiState.value.dialogModelSelected.model
            )
        }
        closeDialog()
    }

    fun deletePromptFromDb(promptItem: PromptItem) {
        saveDeletedToTemp(promptItem)
        viewModelScope.launch {
            database.deletePromptById(promptItem.id)
        }
    }

    private suspend fun prepopulateOnFirstLaunch() {
            val preference = preferencesDataStoreHelper.getFirstPreference(
                FIRST_LAUNCH_KEY,
                false
            )
            if (!preference) {
                prepopulateDatabase.execute()
                preferencesDataStoreHelper.putPreference(FIRST_LAUNCH_KEY, true)
            }
    }

}