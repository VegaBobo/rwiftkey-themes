package rwiftkey.themes.ui.screen.home

import androidx.compose.runtime.mutableStateListOf

enum class HomeToast {
    NONE,
    INSTALLATION_FINISHED,
    INSTALLATION_FAILED
}

enum class AppOperationMode {
    INCOMPATIBLE,
    ROOT,
    XPOSED
}

data class HomeUIState(
    val operationMode: AppOperationMode = AppOperationMode.XPOSED,
    val homeToast: HomeToast = HomeToast.NONE,
    val keyboardThemes: MutableList<KeyboardTheme> = mutableStateListOf(),
    val isLoadingVisible: Boolean = false,
    val hasNoKeyboardsAvail: Boolean = false,
)
