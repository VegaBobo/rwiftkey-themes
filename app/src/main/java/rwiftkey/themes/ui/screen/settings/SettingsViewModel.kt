package rwiftkey.themes.ui.screen.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rwiftkey.themes.core.SKeyboardManager
import rwiftkey.themes.model.SimpleApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rwiftkey.themes.xposed.IntentAction
import rwiftkey.themes.core.startSKActivity
import rwiftkey.themes.rootservice.PrivilegedProvider
import javax.inject.Inject

@HiltViewModel
open class SettingsViewModel @Inject constructor(
    val app: Application,
    private val sKeyboardManager: SKeyboardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    private fun setupInitialUiState() {
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

    fun onClickClean() {
        _uiState.update { it.copy(settingToast = SettingToast.PLEASE_WAIT) }
        viewModelScope.launch {
            if (sKeyboardManager.isRooted()) {
                val targetKeyboard = sKeyboardManager.getPackage()
                PrivilegedProvider.run {
                    cleanThemes(targetKeyboard)
                    _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
                    forceStopPackage(targetKeyboard)
                }
                return@launch
            }

            app.startSKActivity(
                sKeyboardManager.getPackage(),
                IntentAction.CLEAN_UP,
                IntentAction.EXIT_PROCESS
            )
            delay(3000)
            app.startSKActivity(sKeyboardManager.getPackage(), IntentAction.OPEN_THEME_SECTION)
            _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
        }
    }

    fun setToastState(settingToast: SettingToast) {
        _uiState.update { it.copy(settingToast = settingToast) }
    }

}