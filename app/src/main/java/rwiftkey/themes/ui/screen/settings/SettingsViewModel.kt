package rwiftkey.themes.ui.screen.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rwiftkey.themes.ISettingsCallbacks
import rwiftkey.themes.core.Session
import rwiftkey.themes.core.logd
import rwiftkey.themes.core.requestRemoteBinding
import rwiftkey.themes.core.shellStartSKActivity
import rwiftkey.themes.model.SimpleApplication
import rwiftkey.themes.remoteservice.RemoteServiceProvider
import rwiftkey.themes.rootservice.PrivilegedProvider
import rwiftkey.themes.ui.screen.home.OperationMode
import javax.inject.Inject

@HiltViewModel
open class SettingsViewModel @Inject constructor(
    val app: Application,
    private val session: Session
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    private fun setupInitialUiState() {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKeyboard = session.obtainTargetKeyboard()) }
            _uiState.update { it.copy(availableKeyboards = session.availKeyboards) }
        }
    }

    init {
        setupInitialUiState()
        if (session.isXposed())
            registerRemoteCallbacks()
    }

    private fun registerRemoteCallbacks() {
        RemoteServiceProvider.run {
            registerSettingsCallbacks(object : ISettingsCallbacks.Stub() {
                override fun onRequestCleanupFinish() {
                    _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
                }

                override fun onRemoteRequestRebind() {
                    viewModelScope.launch {
                        requestRemoteBinding(
                            targetPackageName = session.targetKeyboardPackage,
                            app = app,
                            shouldOpenThemes = true
                        )
                    }
                }
            })
        }
    }

    fun onToggleDialog() {
        val newDialogValue = !uiState.value.isDialogVisible
        _uiState.update { it.copy(isDialogVisible = newDialogValue) }
    }

    fun onClickKeyboardSelection(
        target: SimpleApplication,
        onChangeXposedTarget: () -> Unit = {}
    ) {
        logd(this, "onClickKeyboardSelection()", target)
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKeyboard = target) }
            if (session.targetKeyboardPackage != target.packageName) {
                session.setTargetKeyboard(target)
                session.updateTargetKeyboardPackage()
                if (session.isXposed()) {
                    session.operationMode = OperationMode.NONE
                    onChangeXposedTarget()
                }
            }
        }
        onToggleDialog()
    }

    private fun cleanThemesRoot() {
        val targetKeyboard = session.targetKeyboardPackage
        PrivilegedProvider.run {
            cleanThemes(targetKeyboard)
            _uiState.update { it.copy(settingToast = SettingToast.THEMES_CLEANED) }
            forceStopPackage(targetKeyboard)
            shellStartSKActivity(session.targetKeyboardPackage, true)
        }
    }

    fun onClickClean() {
        when (session.operationMode) {
            OperationMode.ROOT ->
                cleanThemesRoot()

            OperationMode.XPOSED ->
                RemoteServiceProvider.run { requestCleanup() }

            else -> {}
        }
    }

    fun setToastState(settingToast: SettingToast) {
        _uiState.update { it.copy(settingToast = settingToast) }
    }

}
