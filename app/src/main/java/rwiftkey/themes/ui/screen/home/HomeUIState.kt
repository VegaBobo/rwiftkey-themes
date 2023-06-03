package rwiftkey.themes.ui.screen.home

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
    val isLoadingVisible: Boolean = false,
    val hasNoKeyboardsAvail: Boolean = false,
)
