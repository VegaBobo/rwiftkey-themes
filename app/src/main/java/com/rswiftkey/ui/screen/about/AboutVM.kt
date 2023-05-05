package com.rswiftkey.ui.screen.about

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AboutVM : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUIState())
    val uiState: StateFlow<AboutUIState> = _uiState.asStateFlow()

    var easterEggCounter = 0

    fun increaseEasterEggCounter() {
        easterEggCounter++
        if(easterEggCounter >= 8)
            _uiState.update { it.copy(isEasterEggVisible = true) }
    }

    fun toggleLibrariesDialog() {
        val newDialogVisibilityState = !uiState.value.isLibrariesDialogVisible
        _uiState.update { it.copy(isLibrariesDialogVisible = newDialogVisibilityState) }
    }

}