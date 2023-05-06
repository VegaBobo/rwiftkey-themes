package rwiftkey.themes.ui.screen.settings

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import rwiftkey.themes.core.SKeyboardManager
import rwiftkey.themes.model.SimpleApplication
import rwiftkey.themes.installation.root.ThemesOp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rwiftkey.themes.core.AppPreferences
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
            if (Shell.getShell().isRoot) {
                val targetKeyboard = sKeyboardManager.getPackage()
                ThemesOp(app, null, targetKeyboard).clearThemes()
                _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
                return@launch
            }

            val i = Intent()
            i.setClassName(sKeyboardManager.getPackage(), "com.touchtype.LauncherActivity")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("cleanup", true)
            i.putExtra("exitProcess", true)
            app.startActivity(i)
            delay(3000)
            i.removeExtra("cleanup")
            i.removeExtra("exitProcess")
            i.putExtra("openThemesSection", true)
            app.startActivity(i)
            _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
        }
    }

    fun setToastState(settingToast: SettingToast) {
        _uiState.update { it.copy(settingToast = settingToast) }
    }

}