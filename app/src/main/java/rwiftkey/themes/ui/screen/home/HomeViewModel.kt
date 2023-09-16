package rwiftkey.themes.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beust.klaxon.Klaxon
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.IHomeCallbacks
import rwiftkey.themes.core.SKeyboardManager
import rwiftkey.themes.core.copyFile
import rwiftkey.themes.core.downloadFile
import rwiftkey.themes.core.requestRemoteBinding
import rwiftkey.themes.core.startSKActivity
import rwiftkey.themes.remoteservice.RemoteServiceProvider
import rwiftkey.themes.rootservice.PrivilegedProvider
import rwiftkey.themes.xposed.IntentAction
import java.io.File
import java.io.StringReader
import java.net.URL
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    val app: Application,
    val sKeyboardManager: SKeyboardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(hasNoKeyboardsAvail = !sKeyboardManager.hasKeyboardsAvailable()) }
        viewModelScope.launch(Dispatchers.IO) {
            if (Shell.getShell().isRoot) {
                _uiState.update { it.copy(operationMode = AppOperationMode.ROOT) }
                sKeyboardManager.operationMode = AppOperationMode.ROOT
                loadThemesRoot()
                return@launch
            }
            _uiState.update { it.copy(operationMode = AppOperationMode.INCOMPATIBLE) }
            sKeyboardManager.operationMode = AppOperationMode.INCOMPATIBLE
        }
    }

    fun updateSelectedTheme(keyboardTheme: KeyboardTheme?) {
        _uiState.update { it.copy(selectedTheme = keyboardTheme, isPatchMenuVisible = false) }
    }

    fun loadThemesRoot() {
        PrivilegedProvider.run {
            val keyboardThemes = getKeyboardThemes(sKeyboardManager.getPackage())
            _uiState.update { it.copy(keyboardThemes = keyboardThemes) }
        }
    }

    fun onClickOpenTheme() {
        viewModelScope.launch {
            if (sKeyboardManager.isRooted()) {
                sKeyboardManager.startSKThemeAc()
                return@launch
            }
            app.startSKActivity(sKeyboardManager.getPackage(), IntentAction.OPEN_THEME_SECTION)
        }
    }

    fun onFileSelected(uri: Uri) {
        _uiState.update { it.copy(isInstallationLoadingVisible = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val targetPackage = sKeyboardManager.getPackage()
            try {
                when (uiState.value.operationMode) {
                    AppOperationMode.ROOT -> {
                        val newThemeAbs =
                            copyThemeZipToFilesDir(uri, targetPackage)?.second ?: return@launch
                        PrivilegedProvider.run {
                            installTheme(targetPackage, newThemeAbs)
                            forceStopPackage(targetPackage)
                            loadThemesRoot()
                            setToastState(HomeToast.INSTALLATION_FINISHED)
                        }
                    }

                    AppOperationMode.XPOSED -> {
                        installThemeXposed(uri, targetPackage)
                    }

                    else -> {
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    BuildConfig.APPLICATION_ID,
                    "Error trying to install theme: \n" + e.stackTraceToString()
                )
                setToastState(HomeToast.INSTALLATION_FAILED)
            }
            _uiState.update { it.copy(isInstallationLoadingVisible = false) }
        }
    }

    private fun installThemeXposed(uri: Uri, targetPackage: String) {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        val copiedFileUri = copyThemeZipToFilesDir(uri, targetPackage)?.first ?: return
        RemoteServiceProvider.run {
            requestInstallThemeFromUri(copiedFileUri)
        }
    }

    private fun copyThemeZipToFilesDir(
        uri: Uri,
        targetPackage: String
    ): Pair<Uri /* copied file uri*/, String /* copied file absolute path*/>? {
        val remoteFile = DocumentFile.fromSingleUri(app, uri)
        val ourFilesDir = DocumentFile.fromFile(app.filesDir)
        val localFile = ourFilesDir.createFile("application/zip", "theme")
        app.copyFile(remoteFile!!.uri, localFile!!.uri) ?: return null

        val copiedFile = File(app.filesDir.path + "/theme.zip")
        val ourProvider = BuildConfig.APPLICATION_ID + ".provider"
        val copiedFileUri = FileProvider.getUriForFile(app, ourProvider, copiedFile)

        app.grantUriPermission(targetPackage, copiedFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return Pair(copiedFileUri, app.filesDir.path + "/theme.zip")
    }

    fun setToastState(toast: HomeToast) {
        _uiState.update { it.copy(homeToast = toast) }
    }

    fun onClickSwitchToXposed() {
        initializeSelfServiceCallbacks()
    }

    fun onClickDeleteThemeRoot() {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        val selectedTheme = _uiState.value.selectedTheme?.name ?: return
        PrivilegedProvider.run {
            deleteTheme(sKeyboardManager.getPackage(), selectedTheme)
            updateSelectedTheme(null)
            loadThemesRoot()
            _uiState.update { it.copy(isLoadingOverlayVisible = false) }
        }
    }

    fun onClickPatchTheme() {
        val newPatchMenuValue = !_uiState.value.isPatchMenuVisible

        if (!uiState.value.hasAlreadyLoadedPatches) {
            _uiState.update { it.copy(isLoadingOverlayVisible = true) }
            viewModelScope.launch(Dispatchers.IO) { loadAddonsFromUrl() }
        } else {
            _uiState.update { it.copy(isPatchMenuVisible = newPatchMenuValue) }
        }

        if (!newPatchMenuValue)
            return
    }

    fun loadAddonsFromUrl() {
        val addons = "https://localhost/addons.json"
        val remoteJson = try {
            URL(addons).readText()
        } catch (e: Exception) {
            Log.e(BuildConfig.APPLICATION_ID, "loadAddonsFromUrl(): ${e.stackTraceToString()}")
            null
        }

        if (remoteJson != null) {
            val klaxon = Klaxon()
            val jsonParsedObject = klaxon.parseJsonObject(StringReader(remoteJson))
            for (obj in jsonParsedObject) {
                val addonsArray = jsonParsedObject.array<Any>(obj.key) ?: return
                val patches =
                    addonsArray.let { klaxon.parseFromJsonArray<ThemePatch>(it) } ?: return
                val thisCollection = PatchCollection(obj.key, patches)
                _uiState.value.patchCollection.add(thisCollection)
            }
        } else {
            // todo when cannot reach URL()
        }
        _uiState.update {
            it.copy(
                isPatchMenuVisible = remoteJson != null,
                hasAlreadyLoadedPatches = remoteJson != null,
                isLoadingOverlayVisible = false
            )
        }
    }

    fun onClickApplyPatch(themePatch: ThemePatch) {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        if (uiState.value.operationMode == AppOperationMode.ROOT) {
            PrivilegedProvider.run {
                val addonFile = File(app.filesDir.path + "/addon.zip")
                if (addonFile.exists())
                    addonFile.delete()
                downloadFile(themePatch.url, addonFile.absolutePath)

                modifyTheme(
                    sKeyboardManager.getPackage(),
                    uiState.value.selectedTheme!!.id,
                    addonFile.absolutePath
                )

                _uiState.update {
                    it.copy(
                        isPatchMenuVisible = false,
                        homeToast = HomeToast.PATCHED_SUCCESS,
                        isLoadingOverlayVisible = false
                    )
                }
            }
            return
        }
    }

    fun initializeSelfServiceCallbacks() {
        RemoteServiceProvider.run {
            registerHomeCallbacks(object : IHomeCallbacks.Stub() {
                override fun onRemoteBoundService() {
                    // Remote has bound to our service
                    RemoteServiceProvider.isRemoteLikelyConnected = true
                    _uiState.update { it.copy(operationMode = AppOperationMode.XPOSED) }
                }

                override fun onReceiveThemes(themes: MutableList<KeyboardTheme>?) {
                    Log.d(BuildConfig.APPLICATION_ID, "HomeViewModel.onReceiveThemes(): $themes")
                    if (themes == null) return
                    _uiState.update { it.copy(keyboardThemes = themes) }
                }

                override fun onInstallThemeResult(hasInstalled: Boolean) {
                    _uiState.update {
                        it.copy(homeToast = if (hasInstalled) HomeToast.INSTALLATION_FINISHED else HomeToast.INSTALLATION_FAILED)
                    }
                }

                // some operations may require app restart.
                // onRemoteRequestRebind is called when remote is about to call exitProcess(0)
                // then we can rebind
                override fun onRemoteRequestRebind() {
                    viewModelScope.launch {
                        requestRemoteBinding(
                            targetPackageName = sKeyboardManager.getPackage(),
                            app = app,
                            onFinish = { _uiState.update { it.copy(isLoadingOverlayVisible = false) } }
                        )
                    }
                }
            })
        }
    }

    fun onClickToggleThemes() {
        val newHomeThemesVisibility = !uiState.value.isHomeThemesVisible
        _uiState.update { it.copy(isHomeThemesVisible = newHomeThemesVisibility) }
    }

    fun onReceiveKeyboardThemes(keyboardThemes: ArrayList<KeyboardTheme>) {
        Log.d(BuildConfig.APPLICATION_ID, "onReceiveKeyboardThemes $keyboardThemes")
    }

}