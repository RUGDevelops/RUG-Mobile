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
import eu.virtusdevelops.rug_mobile.screens.MainScreen
import eu.virtusdevelops.rug_mobile.screens.auth.SplashScreen
import eu.virtusdevelops.rug_mobile.screens.auth.LoginScreen
import eu.virtusdevelops.rug_mobile.screens.home.PackageHolderScreen
import eu.virtusdevelops.rug_mobile.screens.home.PackageHoldersScreen
import eu.virtusdevelops.rug_mobile.screens.auth.RegisterScreen
import eu.virtusdevelops.rug_mobile.screens.home.PackageListView

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION
    ) {


//        navigation(
//            startDestination = Screen.LoginScreen.route,
//            route = "auth"
//        ){
//
//        }

        authNavGraph(navController)


        composable(Graph.HOME) {
            MainScreen()
        }


//        composable(Screen.PackageHoldersScreen.route){
//            PackageHoldersScreen(navController)
//        }
//        composable(
//            route = Screen.PackageHolderScreen.route,
//            arguments = listOf(navArgument("packageHolderID") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val packageHolderID = backStackEntry.arguments?.getInt("packageHolderID") ?: return@composable
//            PackageHolderScreen(navController, packageHolderID)
//        }
    }
}


@Composable
fun SetupHomeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
){
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = Screen.PackageHoldersScreen.route
    ){
        composable(Screen.PackageHoldersScreen.route){
            PackageHoldersScreen(navController, innerPadding)
        }
        composable(Screen.PackagesListScreen.route){
            PackageListView(navController = navController, innerPaddingValues = innerPadding)
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
}

