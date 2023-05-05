package com.rswiftkey.ui.screen.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rswiftkey.SKeyboardManager
import com.rswiftkey.SimpleApplication
import com.rswiftkey.ThemesOp
import com.rswiftkey.ui.screen.settings.SettingsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SettingsVM @Inject constructor(
    val app: Application,
    private val sKeyboardManager: SKeyboardManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    fun setupInitialUiState() {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKeyboard = sKeyboardManager.obtainTargetKeyboard()) }
            _uiState.update { it.copy(availableKeyboards = sKeyboardManager.availKeyboards) }
        }
    }

    init {
        setupInitialUiState()
    }

    fun onToggleDialog() {
        val newDialogValue = !uiState.value.isDialogVisible
        _uiState.update { it.copy(isDialogVisible = newDialogValue) }
    }

    fun onClickKeyboardSelection(target: SimpleApplication) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKeyboard = target) }
            sKeyboardManager.setTargetKeyboard(target)
        }
        onToggleDialog()
    }

    fun onClickClean(
        onBeforeClean: () -> Unit = {},
        onAfterClean: () -> Unit = {}
    ) {
        onBeforeClean()
        viewModelScope.launch {
            val targetKeyboard = sKeyboardManager.getPackage()
            ThemesOp(app, null, targetKeyboard).clearThemes()
            onAfterClean()
        }
    }

}