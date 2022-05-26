package com.rswiftkey.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

class CustomColors(private val cs: ColorScheme) {

    fun overrideBackground(color: Color): ColorScheme {
        return ColorScheme(
            primary = cs.primary,
            onPrimary = cs.onPrimary,
            primaryContainer = cs.primaryContainer,
            onPrimaryContainer = cs.onPrimaryContainer,
            inversePrimary = cs.inversePrimary,
            secondary = cs.secondary,
            onSecondary = cs.onSecondary,
            secondaryContainer = cs.secondaryContainer,
            onSecondaryContainer = cs.onSecondaryContainer,
            tertiary = cs.tertiary,
            onTertiary = cs.onTertiary,
            tertiaryContainer = cs.tertiaryContainer,
            onTertiaryContainer = cs.onTertiaryContainer,
            background = color,
            onBackground = cs.onBackground,
            surface = color,
            onSurface = cs.onSurface,
            surfaceVariant = cs.surfaceVariant,
            onSurfaceVariant = cs.onSurfaceVariant,
            surfaceTint = cs.surfaceTint,
            inverseSurface = cs.inverseSurface,
            inverseOnSurface = cs.inverseOnSurface,
            error = cs.error,
            onError = cs.onError,
            errorContainer = cs.errorContainer,
            onErrorContainer = cs.onErrorContainer,
            outline = cs.outline,
        )
    }

}