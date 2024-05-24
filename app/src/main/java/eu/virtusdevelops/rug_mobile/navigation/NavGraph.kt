package eu.virtusdevelops.rug_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.virtusdevelops.rug_mobile.screens.MainScreen
import eu.virtusdevelops.rug_mobile.screens.Screen
import eu.virtusdevelops.rug_mobile.screens.SplashScreen
import eu.virtusdevelops.rug_mobile.screens.LoginScreen
import eu.virtusdevelops.rug_mobile.screens.RegisterScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
//    val vm = LocalUserState.current
//    var startDestination by remember { mutableStateOf(Screen.LoginScreen.route) }
//
//    if(vm.isLoggedIn){
//        startDestination = Screen.MainScreen.route
//    }else{
//        startDestination = Screen.LoginScreen.route
//    }

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
    }
}
