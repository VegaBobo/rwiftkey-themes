package rwiftkey.themes.ui.screen.home

enum class HomeToast {
    NONE,
    INSTALLATION_FINISHED,
    INSTALLATION_FAILED
}

data class HomeUIState(
    val isCompatible: Boolean = true,
    val homeToast: HomeToast = HomeToast.NONE,
    val isLoadingVisible: Boolean = false
)
