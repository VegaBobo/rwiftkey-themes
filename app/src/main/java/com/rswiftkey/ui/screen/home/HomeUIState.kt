package com.rswiftkey.ui.screen.home

enum class HomeToast {
    NONE,
    INSTALLATION_FINISHED,
    INSTALLATION_FAILED
}

data class HomeUIState(
    val homeToast: HomeToast = HomeToast.NONE,
    val isLoadingVisible: Boolean = false
)
