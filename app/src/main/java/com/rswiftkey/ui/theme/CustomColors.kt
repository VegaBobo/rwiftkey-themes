package com.rswiftkey.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

class CustomColors(private val cs: ColorScheme) {

    fun overrideBackground(color: Color): ColorScheme {
        return cs
    }

}