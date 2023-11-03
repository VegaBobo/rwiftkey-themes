package rwiftkey.themes.ui.screen.home

import androidx.compose.runtime.mutableStateListOf
import rwiftkey.themes.model.Theme

enum class HomeToast {
    NONE,
    INSTALLATION_FINISHED,
    INSTALLATION_FAILED,
    PATCHED_SUCCESS,
    PATCHED_FAILED,
    WORKING
}

enum class AppOperationMode {
    NONE,
    ROOT,
    XPOSED
}

data class ThemePatch(
    val title: String = "",
    val thumbnail: String = "",
    val url: String = "",
    val debugOnly: Boolean = false
)

data class PatchCollection(
    val title: String = "",
    val patches: List<ThemePatch> = mutableStateListOf(),
    val selectedPatch: Int = -1
)

data class HomeUIState(
    val operationMode: AppOperationMode = AppOperationMode.XPOSED,
    val homeToast: HomeToast = HomeToast.NONE,
    val keyboardThemes: MutableList<Theme> = mutableStateListOf(),
    val isInstallationLoadingVisible: Boolean = false,
    val hasNoKeyboardsAvail: Boolean = false,
    val selectedTheme: Theme? = null,
    val isThemeDetailsVisible: Boolean = false,
    val hasAlreadyLoadedPatches: Boolean = false,
    val patchCollection: MutableList<PatchCollection> = mutableStateListOf(),
    val isLoadingOverlayVisible: Boolean = false,
    val isHomeThemesVisible: Boolean = false
)
