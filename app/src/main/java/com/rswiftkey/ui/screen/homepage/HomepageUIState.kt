package com.rswiftkey.ui.screen.homepage

enum class HomepageToast {
    NONE,
    INSTALLATION_FINISHED,
    INSTALLATION_FAILED
}

data class HomepageUIState(
    val homepageToast: HomepageToast = HomepageToast.NONE,
    val isLoadingVisible: Boolean = false
)
