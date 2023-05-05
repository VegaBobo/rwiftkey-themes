package rwiftkey.themes.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import rwiftkey.themes.ui.screen.about.AboutScreen
import rwiftkey.themes.ui.screen.home.HomepageScreen
import rwiftkey.themes.ui.screen.settings.SettingsScreen

object Destinations {
    const val Homepage = "/"
    const val Settings = "settings"
    const val About = "about"
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
            HomepageScreen(
                onClickSettings = { navController.navigate(Destinations.Settings) }
            )
        }
        composable(Destinations.Settings) {
            SettingsScreen(
                onAboutClick = { navController.navigate(Destinations.About) }
            )
        }
        composable(Destinations.About) {
            AboutScreen()
        }
    }
}