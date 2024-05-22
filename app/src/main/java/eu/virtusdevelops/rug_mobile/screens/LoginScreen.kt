package eu.virtusdevelops.rug_mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false)}
    var passwordVisibility by remember { mutableStateOf(false) }
    val viewModel = LocalUserState.current


    val modifier = Modifier.padding(5.dp)

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //make bold text with different font
        Text(text = "LOGIN", modifier = modifier
            .padding(10.dp)
            .padding(5.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = (modifier.fillMaxSize(0.1f)))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Email") },
            placeholder = { Text("Your email here")},
            modifier = modifier,
            isError = isError,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Username")
            })
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Your password here")},
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = modifier,
            isError = isError,
            leadingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(Icons.Filled.Lock, contentDescription = "Toggle password visibility")
                }
            }
        )
        Spacer(modifier = modifier.fillMaxSize(0.7f))
        val context = LocalContext.current
        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = {
                viewModel.login(username, password)


//                GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//                    val response = Api.api.login(LoginRequest(
//                        username,
//                        password
//                    ))
//
//                    if(response.isSuccessful){
//                        // save user into app state
//
//                        withContext(Dispatchers.Main){
//                            isError = false
//
//
//                            // navigate without backtrace (so user cant click back and go to login again
//                            navController.navigate(
//                                Screen.MainScreen.route){
//
//                                popUpTo(Screen.LoginScreen.route) { // Make sure this matches your login screen's route
//                                    inclusive = true
//                                }
//
//                            }
//
//                        }
//                    }else{
//                        val error = response.getErrorResponse<ErrorResponse>()
//
//
//
//                        withContext(Dispatchers.Main){
//                            isError = true
//                            if(error?.errors != null){
//                                Toast.makeText(context, error.errors.toString(), Toast.LENGTH_LONG).show()
//                            }else{
//                                Toast.makeText(context, response.code().toString(), Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//
//                }
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


val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
    throwable.printStackTrace()
}




@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}