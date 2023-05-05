package com.rswiftkey.ui.settings

import androidx.compose.runtime.mutableStateListOf
import com.rswiftkey.SimpleApplication

data class SettingsUIState(
    val selectedKeyboard: SimpleApplication = SimpleApplication(),
    val availableKeyboards: MutableList<SimpleApplication> = mutableStateListOf(),
    val isDialogVisible: Boolean = false
)