package eu.virtusdevelops.rug_mobile.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel


@Composable
fun SplashScreen(navController: NavController) {
    val viewModel = hiltViewModel<UserViewModel>()
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
                    navController.navigate(Graph.HOME) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
            else -> {
                // Ensure this only triggers navigation once
                LaunchedEffect(Unit) {
                    navController.navigate(AuthGraph.LoginScreen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}
