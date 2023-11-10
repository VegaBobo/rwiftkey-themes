package rwiftkey.themes.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
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
import rwiftkey.themes.core.Constants
import rwiftkey.themes.core.Session
import rwiftkey.themes.core.copyFile
import rwiftkey.themes.core.downloadFile
import rwiftkey.themes.core.hasConnection
import rwiftkey.themes.core.logd
import rwiftkey.themes.core.loge
import rwiftkey.themes.core.requestRemoteBinding
import rwiftkey.themes.core.shellStartSKActivity
import rwiftkey.themes.core.startSKActivity
import rwiftkey.themes.model.Theme
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
    val session: Session
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    init {
        // Check if user has at least a available keyboard app installed.
        val hasAvailKeyboards = session.hasKeyboardsAvailable()
        _uiState.update { it.copy(hasNoKeyboardsAvail = !hasAvailKeyboards) }

        // Check if root is available and granted.
        viewModelScope.launch(Dispatchers.IO) {
            if (Shell.getShell().isRoot) {
                _uiState.update { it.copy(operationMode = OperationMode.ROOT) }
                session.operationMode = OperationMode.ROOT
                return@launch
            }

            // If root is not available, says it is incompatible
            // from incompatible, user can setup Xposed operation mode.
            _uiState.update { it.copy(operationMode = OperationMode.NONE) }
            session.operationMode = OperationMode.NONE
        }
    }

    fun updateSelectedTheme(theme: Theme?) {
        _uiState.update { it.copy(selectedTheme = theme, isPatchMenuVisible = false) }
    }

    fun setToastState(toast: HomeToast) {
        _uiState.update { it.copy(homeToast = toast) }
    }

    fun onClickShowThemes() {
        // Xposed operation mode doesn't require theme loading
        // it receives themes from remote app when service is bound.
        if (session.isRooted()) {
            loadThemesRoot()
        }

        val newHomeThemesVisibility = !uiState.value.isHomeThemesVisible
        _uiState.update { it.copy(isHomeThemesVisible = newHomeThemesVisibility) }
    }

    fun onClickOpenThemesSection() {
        val targetKeyboard = session.targetKeyboardPackage
        when (session.operationMode) {
            OperationMode.ROOT ->
                shellStartSKActivity(targetKeyboard)

            OperationMode.XPOSED ->
                app.startSKActivity(targetKeyboard, IntentAction.OPEN_THEME_SECTION)

            else -> {}
        }
    }

    fun onFileSelected(uri: Uri, onFinish: () -> Unit = {}) {
        _uiState.update { it.copy(isInstallationLoadingVisible = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val targetPackage = session.targetKeyboardPackage
            try {
                when (session.operationMode) {
                    OperationMode.ROOT ->
                        installThemeRoot(uri, targetPackage)

                    OperationMode.XPOSED ->
                        installThemeXposed(uri, targetPackage)

                    else -> {}
                }
            } catch (e: Exception) {
                loge(this, "Error trying to install theme:", e.stackTraceToString())
                setToastState(HomeToast.INSTALLATION_FAILED)
            }
            _uiState.update { it.copy(isInstallationLoadingVisible = false) }
            onFinish()
        }
    }

    private fun loadAddonsFromUrl() {
        val remoteJson = try {
            URL(Constants.ADDONS_URL).readText()
        } catch (e: Exception) {
            loge(this, "loadAddonsFromUrl()", e.stackTraceToString())
            null
        }

        if (remoteJson != null) {
            val klaxon = Klaxon()
            val jsonParsedObject = klaxon.parseJsonObject(StringReader(remoteJson))
            for (obj in jsonParsedObject) {
                val addonsArray = jsonParsedObject.array<Any>(obj.key) ?: return
                val patches =
                    addonsArray.let { klaxon.parseFromJsonArray<ThemePatch>(it) }
                        ?.mapNotNull { if (BuildConfig.DEBUG || !it.debugOnly) it else null }
                        ?: return
                if (patches.isEmpty()) break
                val thisCollection = PatchCollection(obj.key, patches)
                _uiState.value.patchCollection.add(thisCollection)
            }
        }

        _uiState.update {
            it.copy(
                hasAlreadyLoadedPatches = remoteJson != null,
                isLoadingOverlayVisible = false
            )
        }
    }

    fun onClickDeleteTheme() {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        val selectedTheme = _uiState.value.selectedTheme?.id ?: return
        when (session.operationMode) {
            OperationMode.ROOT ->
                deleteThemeRoot(selectedTheme)

            OperationMode.XPOSED ->
                RemoteServiceProvider.run { requestDeleteTheme(selectedTheme) }

            else -> {}
        }
    }

    fun onClickApplyPatch(themePatch: ThemePatch) {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        val targetTheme = uiState.value.selectedTheme!!.id
        viewModelScope.launch(Dispatchers.IO) {
            val addonFile = File(app.filesDir.path + "/addon.zip")
            if (addonFile.exists()) addonFile.delete()
            downloadFile(themePatch.url, addonFile.absolutePath)

            when (session.operationMode) {
                OperationMode.ROOT ->
                    applyPatchRoot(targetTheme, addonFile.absolutePath)

                OperationMode.XPOSED ->
                    applyPatchXposed(targetTheme, addonFile)

                else -> {}
            }
        }
    }

    fun onClickLoadPatches() {
        val newPatchMenuValue = !_uiState.value.isPatchMenuVisible
        _uiState.update { it.copy(isPatchMenuVisible = newPatchMenuValue) }

        if (!uiState.value.hasAlreadyLoadedPatches && hasConnection(app)) {
            _uiState.update { it.copy(isLoadingOverlayVisible = true) }
            viewModelScope.launch(Dispatchers.IO) { loadAddonsFromUrl() }
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
        val copiedFileUri = FileProvider.getUriForFile(app, Constants.FILE_PROVIDER, copiedFile)

        app.grantUriPermission(targetPackage, copiedFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return Pair(copiedFileUri, app.filesDir.path + "/theme.zip")
    }

    //
    // Xposed operations
    //

    private var hasSelfCallbacksInitialized = false

    fun initializeSelfServiceCallbacks(onReady: () -> Unit) {
        if (hasSelfCallbacksInitialized) return
        RemoteServiceProvider.run {
            registerHomeCallbacks(object : IHomeCallbacks.Stub() {
                override fun onRemoteBoundService() {
                    // Remote has bound to our service, since it happened
                    // we are sure Xposed operation mode is working
                    RemoteServiceProvider.isRemoteLikelyConnected = true
                    session.operationMode = OperationMode.XPOSED
                    _uiState.update { it.copy(operationMode = session.operationMode) }
                }

                override fun onReceiveThemes(themes: List<Theme>?) {
                    logd(this, "onReceiveThemes()", themes)
                    if (themes == null) return
                    _uiState.value.keyboardThemes.clear()
                    _uiState.value.keyboardThemes.addAll(themes)
                }

                override fun onInstallThemeResult(hasInstalled: Boolean) {
                    val result = if (hasInstalled)
                        HomeToast.INSTALLATION_FINISHED
                    else
                        HomeToast.INSTALLATION_FAILED

                    _uiState.update {
                        it.copy(homeToast = result, isLoadingOverlayVisible = false)
                    }
                }

                // some operations may require app restart.
                // onRemoteRequestRebind is called when remote is about to call exitProcess(0)
                // then we can rebind
                override fun onRemoteRequestRebind() {
                    viewModelScope.launch {
                        requestRemoteBinding(
                            targetPackageName = session.targetKeyboardPackage,
                            app = app
                        )
                    }
                }

                override fun onFinishModifyTheme() {
                    _uiState.update {
                        it.copy(
                            isPatchMenuVisible = false,
                            homeToast = HomeToast.PATCHED_SUCCESS,
                            isLoadingOverlayVisible = false,
                            selectedTheme = null
                        )
                    }
                }

                override fun onFinishDeleteTheme() {
                    _uiState.value.keyboardThemes.remove(_uiState.value.selectedTheme)
                    updateSelectedTheme(null)
                    _uiState.update { it.copy(isLoadingOverlayVisible = false) }
                }
            })
            hasSelfCallbacksInitialized = true
            onReady()
        }
    }

    private fun applyPatchXposed(targetThemeId: String, addonFile: File) {
        RemoteServiceProvider.run {
            val addonFileUri = FileProvider.getUriForFile(app, Constants.FILE_PROVIDER, addonFile)
            val targetPkg = session.targetKeyboardPackage
            app.grantUriPermission(
                targetPkg,
                addonFileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            requestModifyTheme(targetThemeId, addonFileUri)
        }
    }

    private fun installThemeXposed(uri: Uri, targetPackage: String) {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        val copiedFileUri = copyThemeZipToFilesDir(uri, targetPackage)?.first ?: return
        RemoteServiceProvider.run {
            requestInstallThemeFromUri(copiedFileUri)
        }
    }

    //
    // Root
    //

    private fun applyPatchRoot(targetThemeId: String, addonZipFileAbs: String) {
        PrivilegedProvider.run {
            modifyTheme(session.targetKeyboardPackage, targetThemeId, addonZipFileAbs)

            _uiState.update {
                it.copy(
                    isPatchMenuVisible = false,
                    homeToast = HomeToast.PATCHED_SUCCESS,
                    selectedTheme = null
                )
            }
            loadThemesRoot()
        }
    }

    private fun deleteThemeRoot(selectedTheme: String) {
        PrivilegedProvider.run {
            val pkg = session.targetKeyboardPackage
            deleteTheme(pkg, selectedTheme)
            updateSelectedTheme(null)
            loadThemesRoot()
            forceStopPackage(pkg)
            _uiState.update { it.copy(isLoadingOverlayVisible = false) }
            shellStartSKActivity(session.targetKeyboardPackage, true)
        }
    }

    private fun loadThemesRoot() {
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }
        PrivilegedProvider.run {
            val keyboardThemes =
                getKeyboardThemes(session.targetKeyboardPackage).sortedBy { it.name }
            _uiState.update {
                it.copy(
                    keyboardThemes = keyboardThemes.toMutableList(),
                    isLoadingOverlayVisible = false
                )
            }
        }
    }

    private fun installThemeRoot(uri: Uri, targetPackage: String) {
        val newThemeAbs =
            copyThemeZipToFilesDir(uri, targetPackage)?.second ?: return
        _uiState.update { it.copy(isLoadingOverlayVisible = true) }

        PrivilegedProvider.run {
            installTheme(targetPackage, newThemeAbs)
            loadThemesRoot()
            forceStopPackage(targetPackage)
            _uiState.update { it.copy(isLoadingOverlayVisible = false) }
            setToastState(HomeToast.INSTALLATION_FINISHED)
            shellStartSKActivity(session.targetKeyboardPackage, true)
        }
    }

}
