package eu.virtusdevelops.rug_mobile.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState
import kotlinx.coroutines.DelicateCoroutinesApi

@Composable
fun RegisterScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val viewModel = LocalUserState.current

    val modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //make bold text with different font
            Text(
                text = "REGISTER", modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = (modifier.fillMaxSize(0.1f)))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Your email here") },
                singleLine = true,
                modifier = modifier,
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Username")
                })
            // first name text field
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                placeholder = { Text("Your first name here") },
                singleLine = true,
                modifier = modifier,
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Username")
                })
            // last name text field
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                placeholder = { Text("Your last name here") },
                singleLine = true,
                modifier = modifier,
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
                leadingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(Icons.Filled.Lock, contentDescription = "Toggle password visibility")
                    }
                }
            )
            TextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                label = { Text("Confirm password") },
                placeholder = { Text("Confirm your password here") },
                singleLine = true,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = modifier,
                leadingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(Icons.Filled.Lock, contentDescription = "Toggle password visibility")
                    }
                }
            )

        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    viewModel.register(email, firstName, lastName, password, repeatPassword, onSuccess = {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.SplashScreen.route)
                    })
                },
                modifier = modifier
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (viewModel.isBusy) {
                    CircularProgressIndicator()
                } else {
                    Text("Register")
                }
                Text("Register")
            }

            ClickableText(
                text = AnnotatedString("Already have an account? Login"),
                modifier = Modifier.padding(5.dp),
                style = TextStyle(fontWeight = FontWeight.Bold),
                onClick = {
                    navController.navigate(Screen.LoginScreen.route)
                })
        }

    }
}

@Preview
@Composable
fun PreviewRegisterView() {
    RegisterScreen(navController = rememberNavController())
}