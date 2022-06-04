package com.rswiftkey.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.rswiftkey.SKeyboard
import com.rswiftkey.ThemesOp
import com.rswiftkey.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomepageVM
@Inject constructor(application: Application) :
    AndroidViewModel(application) {

    protected val context get() = getApplication<Application>()

    fun addTheme(uri: Uri, sKeyboard: SKeyboard) {
        viewModelScope.launch {
            ThemesOp(
                context,
                uri,
                sKeyboard.getPackage(context)
            ).install()
        }
    }

    fun openThemeSection() {
        //Util.startSKActivity()
    }

    fun openSettings(navController: NavHostController) {
        navController.navigate(Destinations.Settings)
    }

}