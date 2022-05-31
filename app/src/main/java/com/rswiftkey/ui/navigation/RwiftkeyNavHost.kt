package com.rswiftkey.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rswiftkey.ui.homepage.HomepageScreen
import com.rswiftkey.ui.settings.SettingsScreen

object Destinations {
    const val Homepage = "/"
    const val Settings = "settings"
}

@Composable
fun RwiftkeyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.Homepage
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.Homepage) {
            HomepageScreen {
                navController.navigate(Destinations.Settings)
            }
        }

        composable(Destinations.Settings) {
            SettingsScreen()
        }
    }
}