package eu.virtusdevelops.rug_mobile.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState


@Composable
fun SplashScreen(navController: NavController) {
    val viewModel = LocalUserState.current
    val isBusy by viewModel::isBusy
    val isLoggedIn by viewModel::isLoggedIn

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isBusy -> {
                CircularProgressIndicator()
            }
            isLoggedIn -> {
                // Ensure this only triggers navigation once
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
            else -> {
                // Ensure this only triggers navigation once
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}
