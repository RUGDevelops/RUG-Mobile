package eu.virtusdevelops.rug_mobile.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.passwordValidation.PasswordValidationState
import eu.virtusdevelops.rug_mobile.passwordValidation.PasswordValidator
import eu.virtusdevelops.rug_mobile.screens.GradientBackground
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(PasswordValidationState()) }

    isError = oldPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()

    LaunchedEffect(newPassword, confirmPassword) {
        passwordError = PasswordValidator.execute(newPassword, confirmPassword)
    }

    val modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()

    /// var viewModel = hiltViewModel<UserViewModel>()

    GradientBackground {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHANGE PASSWORD",
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(bottom = 20.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                TextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Old password") },
                    placeholder = { Text("Your old password here") },
                    singleLine = true,
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username")
                    })
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New password") },
                    placeholder = { Text("Your new password here") },
                    singleLine = true,
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username")
                    }
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    placeholder = { Text("Confirm your new password here") },
                    singleLine = true,
                    modifier = modifier,
                    isError = isError,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username")
                    }
                )

                if (isError) {
                    Text(
                        "Fields must not be empty",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .padding(top = 10.dp)
                ) {
                    PasswordValidationRow("Passwords match", passwordError.matching)
                    PasswordValidationRow("Contains uppercase letter", passwordError.hasUpperCaseLetter)
                    PasswordValidationRow("Contains lowercase letter", passwordError.hasLowerCaseLetter)
                    PasswordValidationRow("Contains digit", passwordError.hasDigit)
                    PasswordValidationRow("Contains special character", passwordError.hasSpecialCharacter)
                }
            }

            Button(
                onClick = {
//                    viewModel.changePassword(oldPassword, newPassword, confirmPassword) {
//                        navController.navigate(Graph.AuthGraph.LoginScreen.route)
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Change password",
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

@Preview
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen(navController = rememberNavController())
}