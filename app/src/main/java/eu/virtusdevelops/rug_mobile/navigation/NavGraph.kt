package eu.virtusdevelops.rug_mobile.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import eu.virtusdevelops.rug_mobile.screens.SettingsScreen
import eu.virtusdevelops.rug_mobile.screens.auth.SplashScreen
import eu.virtusdevelops.rug_mobile.screens.auth.LoginScreen
import eu.virtusdevelops.rug_mobile.screens.home.PackageHolderScreen
import eu.virtusdevelops.rug_mobile.screens.home.PackageHoldersScreen
import eu.virtusdevelops.rug_mobile.screens.auth.RegisterScreen
import eu.virtusdevelops.rug_mobile.screens.deliveryPackage.IncomingPackageView
import eu.virtusdevelops.rug_mobile.screens.deliveryPackage.OutgoingPackageView
import eu.virtusdevelops.rug_mobile.screens.deliveryPackage.SendPackageView
import eu.virtusdevelops.rug_mobile.screens.home.OutgoingPackageListView
import eu.virtusdevelops.rug_mobile.screens.home.IncomingPackageListView
import java.util.UUID

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController)
        mainNavGraph(navController, innerPadding)
    }
}



fun NavGraphBuilder.mainNavGraph(navController: NavHostController, innerPadding: PaddingValues){
    navigation(
        route = Graph.HOME,
        startDestination = Screen.PackageHoldersScreen.route
    ){

        packagesNavGraph(navController, innerPadding)

        composable(Screen.PackageHoldersScreen.route){
            PackageHoldersScreen(navController, innerPadding)
        }
        composable(Screen.SettingsScreen.route){
            SettingsScreen(navController, innerPadding)
        }
        composable(
            route = Screen.PackageHolderScreen.route,
            arguments = listOf(navArgument("packageHolderID") { type = NavType.IntType })
        ) { backStackEntry ->
            val packageHolderID = backStackEntry.arguments?.getInt("packageHolderID") ?: return@composable
            PackageHolderScreen(navController, packageHolderID, innerPadding)
        }
    }
}


fun NavGraphBuilder.packagesNavGraph(navController: NavHostController, innerPadding: PaddingValues){
    navigation(
        route = Graph.PACKAGES,
        startDestination = Screen.PackagesOutListScreen.route
    ){
        composable(Screen.PackagesOutListScreen.route) {
            OutgoingPackageListView(navController = navController, innerPaddingValues = innerPadding)
        }
        composable(Screen.PackagesInListScreen.route){
            IncomingPackageListView(navController = navController, innerPaddingValues = innerPadding)
        }

        composable(Screen.SendPackage.route){
            SendPackageView(navController = navController, paddingValues = innerPadding)
        }


        composable(
            route = Screen.IncomingPackageScreen.route,
            arguments = listOf(navArgument("packageID") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageHolderID = UUID.fromString(backStackEntry.arguments?.getString("packageID")) ?: return@composable
            IncomingPackageView(navController, innerPadding, packageHolderID)
        }


        composable(
            route = Screen.OutgoingPackageScreen.route,
            arguments = listOf(navArgument("packageID") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageHolderID = UUID.fromString(backStackEntry.arguments?.getString("packageID")) ?: return@composable
            OutgoingPackageView(navController, innerPadding, packageHolderID)
        }

    }
}


fun NavGraphBuilder.authNavGraph(navController: NavHostController){
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthGraph.SplashScreen.route
    ){
        composable(AuthGraph.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(AuthGraph.RegisterScreen.route){
            RegisterScreen(navController)
        }
        composable(AuthGraph.SplashScreen.route){
            SplashScreen(navController)
        }
    }
}


object Graph{
    const val HOME = "home"
    const val AUTHENTICATION = "auth"
    const val PACKAGES = "packages"
}

