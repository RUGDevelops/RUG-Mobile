package eu.virtusdevelops.rug_mobile.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel


@Composable
fun PackageHolderAddScreen(
    navController: NavController,
) {
    var deviceID by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<UserViewModel>()


    val modifier = Modifier.padding(5.dp)

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //make bold text with different font
        Text(text = "ADD PACKAGE HOLDER", modifier = modifier
            .padding(10.dp)
            .padding(5.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = (modifier.fillMaxSize(0.1f)))
        TextField(
            value = deviceID,
            onValueChange = { deviceID = it },
            label = { Text("Device ID") },
            placeholder = { Text("Enter device id or scan QR code") },
            modifier = modifier,
            isError = viewModel.error != null,
            leadingIcon = {
                Icon(Icons.Filled.Info, contentDescription = "Username")
            })
        Spacer(modifier = modifier.fillMaxSize(0.7f))



        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = {
//                viewModel.login(username, password)
            },
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary)) {

            if(viewModel.isBusy){
                CircularProgressIndicator()
            }else if(viewModel.isLoggedIn){
                navController.navigate(Screen.MainScreen.route) {
                    popUpTo(navController.graph.id)
                }
            }else{
                Text("Login")
            }

        }
    }
}