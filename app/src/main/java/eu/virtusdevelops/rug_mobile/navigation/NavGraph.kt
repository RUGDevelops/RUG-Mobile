package eu.virtusdevelops.rug_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import eu.virtusdevelops.rug_mobile.screens.MainScreen
import eu.virtusdevelops.rug_mobile.screens.Screen
import eu.virtusdevelops.rug_mobile.screens.SplashScreen
import eu.virtusdevelops.rug_mobile.screens.LoginScreen
import eu.virtusdevelops.rug_mobile.screens.PackageHolderInfo
import eu.virtusdevelops.rug_mobile.screens.PackageHolderScreen
import eu.virtusdevelops.rug_mobile.screens.RegisterScreen
import java.util.UUID

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(Screen.MainScreen.route) {
            MainScreen(navController)
        }
        composable(Screen.SplashScreen.route){
            SplashScreen(navController)
        }
        composable(Screen.RegisterScreen.route){
            RegisterScreen(navController)
        }
        composable(
            route = Screen.PackageHolderScreen.route,
            arguments = listOf(navArgument("packageHolderID") { type = NavType.IntType })
        ) { backStackEntry ->
            val packageHolderID = backStackEntry.arguments?.getInt("packageHolderID") ?: return@composable
            PackageHolderScreen(navController, packageHolderID)
        }
    }
}
