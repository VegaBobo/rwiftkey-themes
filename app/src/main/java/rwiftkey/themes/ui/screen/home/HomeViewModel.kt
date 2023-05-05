package rwiftkey.themes.ui.screen.home

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.core.SKeyboardManager
import rwiftkey.themes.installation.root.ThemesOp
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    val app: Application,
    val sKeyboardManager: SKeyboardManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        if(!Shell.getShell().isRoot)
            _uiState.update { it.copy(isCompatible = false) }
    }

    fun onClickOpenTheme() {
        viewModelScope.launch { sKeyboardManager.startSKThemeAc() }
    }

    fun onFileSelected(uri: Uri) {
        _uiState.update { it.copy(isLoadingVisible = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val targetPackage = sKeyboardManager.getPackage()
            try {
                ThemesOp(app, uri, targetPackage).install()
                setToastState(HomeToast.INSTALLATION_FINISHED)
            } catch (e: Exception) {
                Log.e(
                    BuildConfig.APPLICATION_ID,
                    "Error trying to install theme: \n" + e.stackTraceToString()
                )
                setToastState(HomeToast.INSTALLATION_FAILED)
            }
            _uiState.update { it.copy(isLoadingVisible = false) }
        }
    }

    fun setToastState(toast: HomeToast) {
        _uiState.update { it.copy(homeToast = toast) }
    }

}