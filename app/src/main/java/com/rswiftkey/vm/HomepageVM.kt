package com.rswiftkey.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.rswiftkey.SKeyboardManager
import com.rswiftkey.ThemesOp
import com.rswiftkey.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomepageVM @Inject constructor(
    val app: Application,
    val sKeyboardManager: SKeyboardManager,
) : AndroidViewModel(app) {

    fun addTheme(uri: Uri, sKeyboardManager: SKeyboardManager) {
        viewModelScope.launch {
            val targetPackage = sKeyboardManager.getPackage()
            ThemesOp(app, uri, targetPackage).install()
        }
    }

    fun openThemeSection() {
        viewModelScope.launch { sKeyboardManager.startSKThemeAc() }
    }

    fun openSettings(navController: NavHostController) {
        navController.navigate(Destinations.Settings)
    }

}