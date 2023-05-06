package rwiftkey.themes.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
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
import rwiftkey.themes.core.AppPreferences
import rwiftkey.themes.core.SKeyboardManager
import rwiftkey.themes.core.copyFile
import rwiftkey.themes.installation.root.ThemesOp
import java.io.File
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    val app: Application,
    val sKeyboardManager: SKeyboardManager,
    val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (Shell.getShell().isRoot) {
                _uiState.update { it.copy(operationMode = AppOperationMode.ROOT) }
                return@launch
            }
            if (appPreferences.readUseXposed()) {
                _uiState.update { it.copy(operationMode = AppOperationMode.XPOSED) }
                return@launch
            }
            _uiState.update { it.copy(operationMode = AppOperationMode.INCOMPATIBLE) }
        }
    }

    fun onClickOpenTheme() {
        viewModelScope.launch {
            if (Shell.getShell().isRoot) {
                sKeyboardManager.startSKThemeAc()
                return@launch
            }
            val i = Intent()
            i.setClassName(sKeyboardManager.getPackage(), "com.touchtype.LauncherActivity")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("openThemesSection", true)
            app.startActivity(i)
        }
    }

    fun onFileSelected(uri: Uri) {
        _uiState.update { it.copy(isLoadingVisible = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val targetPackage = sKeyboardManager.getPackage()
            try {
                when (uiState.value.operationMode) {
                    AppOperationMode.ROOT -> {
                        ThemesOp(app, uri, targetPackage).install()
                    }

                    AppOperationMode.XPOSED -> {
                        installThemeXposed(uri, targetPackage)
                    }

                    else -> {
                        return@launch
                    }
                }
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

    private fun installThemeXposed(uri: Uri, targetPackage: String) {
        // Cannot open Uri inside hooked app
        // as workaround, we are using FileProvider instead.
        val remoteFile = DocumentFile.fromSingleUri(app, uri)
        val ourFilesDir = DocumentFile.fromFile(app.filesDir)
        val localFile = ourFilesDir.createFile("application/zip", "theme")
        app.copyFile(remoteFile!!.uri, localFile!!.uri) ?: return

        val copiedFile = File(app.filesDir.path + "/theme.zip")
        val ourProvider = BuildConfig.APPLICATION_ID + ".provider"
        val copiedFileUri = FileProvider.getUriForFile(app, ourProvider, copiedFile)

        app.grantUriPermission(targetPackage, copiedFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val i = Intent()
        i.setClassName(targetPackage, "com.touchtype.LauncherActivity")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra("themeFileUri", copiedFileUri)
        i.putExtra("finish", true)
        i.putExtra("exitProcess", true)
        app.startActivity(i)
    }

    fun setToastState(toast: HomeToast) {
        _uiState.update { it.copy(homeToast = toast) }
    }

    fun onClickSwitchToXposed() {
        _uiState.update { it.copy(operationMode = AppOperationMode.XPOSED) }
        viewModelScope.launch { appPreferences.setUseXposed() }
    }

}