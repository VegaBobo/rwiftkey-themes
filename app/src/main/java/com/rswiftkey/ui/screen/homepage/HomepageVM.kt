package com.rswiftkey.ui.screen.homepage

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rswiftkey.BuildConfig
import com.rswiftkey.SKeyboardManager
import com.rswiftkey.ThemesOp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomepageVM @Inject constructor(
    val app: Application,
    val sKeyboardManager: SKeyboardManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomepageUIState())
    val uiState: StateFlow<HomepageUIState> = _uiState.asStateFlow()

    fun onClickOpenTheme() {
        viewModelScope.launch { sKeyboardManager.startSKThemeAc() }
    }

    fun onFileSelected(uri: Uri) {
        _uiState.update { it.copy(isLoadingVisible = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val targetPackage = sKeyboardManager.getPackage()
            try {
                ThemesOp(app, uri, targetPackage).install()
                setToastState(HomepageToast.INSTALLATION_FINISHED)
            } catch (e: Exception) {
                Log.e(
                    BuildConfig.APPLICATION_ID,
                    "Error trying to install theme: \n" + e.stackTraceToString()
                )
                setToastState(HomepageToast.INSTALLATION_FAILED)
            }
            _uiState.update { it.copy(isLoadingVisible = false) }
        }
    }

    fun setToastState(toast: HomepageToast) {
        _uiState.update { it.copy(homepageToast = toast) }
    }

}