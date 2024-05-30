package eu.virtusdevelops.rug_mobile.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel


@Composable
fun SettingsScreen(navController: NavController, innerPaddingValues: PaddingValues) {

    val viewModel = hiltViewModel<UserViewModel>()


    Column(
        modifier = Modifier.padding(innerPaddingValues)
            .fillMaxSize()
    ) {


        Button(
            onClick = {

                viewModel.logout {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(navController.graph.id)
                    }
                }

            },
            shape = RoundedCornerShape(10.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout", fontWeight = FontWeight.Bold)
        }
    }


}