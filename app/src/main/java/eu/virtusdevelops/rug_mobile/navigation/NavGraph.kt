package eu.virtusdevelops.rug_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.virtusdevelops.rug_mobile.screens.MainScreen
import eu.virtusdevelops.rug_mobile.screens.LoginScreen
import eu.virtusdevelops.rug_mobile.screens.Screen
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    val vm = LocalUserState.current
    var startDestination by remember { mutableStateOf(Screen.LoginScreen.route) }

    if(vm.isLoggedIn){
        startDestination = Screen.MainScreen.route
    }else{
        startDestination = Screen.LoginScreen.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(Screen.MainScreen.route) {
            MainScreen(navController)
        }
    }
}
