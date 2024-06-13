package eu.virtusdevelops.rug_mobile.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.screens.GradientBackground
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

@Composable
fun LoginScreen(
    navController: NavController,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    val viewModel = hiltViewModel<UserViewModel>()

    val modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()

    isError = username.isEmpty() && password.isEmpty()


    GradientBackground (

    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //make bold text with different font
                Text(
                    text = "LOGIN", modifier = Modifier.padding(5.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Email") },
                    placeholder = { Text("Your email here") },
                    singleLine = true,
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username")
                    })
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Your password here") },
                    singleLine = true,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(Icons.Filled.Lock, contentDescription = "Toggle password visibility")
                        }
                    }
                )
                if (isError) {
                    Text(
                        "Fields must not be empty",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    enabled = !isError,
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        viewModel.login(username, password)
                    },
                    modifier = modifier
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary)) {

                    if(viewModel.isBusy){
                        CircularProgressIndicator()
                    }else if(viewModel.isLoggedIn){
                        navController.navigate(Graph.HOME) {
                            popUpTo(navController.graph.id)
                        }
                    }else{
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

                ClickableText(
                    modifier = Modifier.padding(9.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                    text = AnnotatedString("Face login"),
                ) {
                    navController.navigate(AuthGraph.FaceLoginScreen.route){
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                ClickableText(
                    modifier = Modifier.padding(9.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                    text = AnnotatedString("Don't have an account? Register"),
                ) {
                    navController.navigate(AuthGraph.RegisterScreen.route){
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }


}


val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}


@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}