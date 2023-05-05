package com.rswiftkey.ui.screen.settings

import androidx.compose.runtime.mutableStateListOf
import com.rswiftkey.model.SimpleApplication

enum class SettingToast {
    NONE,
    PLEASE_WAIT,
    THEMES_CLEANED
}

data class SettingsUIState(
    val settingToast: SettingToast = SettingToast.NONE,
    val selectedKeyboard: SimpleApplication = SimpleApplication(),
    val availableKeyboards: MutableList<SimpleApplication> = mutableStateListOf(),
    val isDialogVisible: Boolean = false
)