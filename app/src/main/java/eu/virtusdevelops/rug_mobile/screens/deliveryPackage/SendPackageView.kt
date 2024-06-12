package eu.virtusdevelops.rug_mobile.screens.deliveryPackage

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.OutgoingPackageListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.PackageViewModel
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

@Composable
fun SendPackageView(
    navController: NavController,
    paddingValues: PaddingValues
) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var packageHolder by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var postNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    val modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()

    var viewModel = hiltViewModel<OutgoingPackageListViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Recipient information",
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = modifier
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name") },
                    modifier = modifier.weight(1f)
                )
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last name") },
                    modifier = modifier.weight(1f)
                )
            }

            TextField(
                value = packageHolder,
                onValueChange = { packageHolder = it },
                label = { Text("Select package holder") },
                modifier = modifier
            )

            TextField(
                value = streetAddress,
                onValueChange = { streetAddress = it },
                label = { Text("Street address") },
                modifier = modifier
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text("House number") },
                    modifier = modifier.weight(1f)
                )
                TextField(
                    value = postNumber,
                    onValueChange = { postNumber = it },
                    label = { Text("Post number") },
                    modifier = modifier.weight(1f)
                )
            }

            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = modifier
            )

            TextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = modifier
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    viewModel.addOutgoingPackage(
                        email,
                        firstName,
                        lastName,
                        packageHolder,
                        streetAddress,
                        houseNumber,
                        postNumber,
                        city,
                        country
                    ) {
                        Toast.makeText(
                            navController.context,
                            "Created package successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    }
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
                }
                else {
                    Text(
                        text = "Create package",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    navController.popBackStack()
                },
                modifier = modifier
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun SendPackageViewPreview() {
    SendPackageView(rememberNavController(), PaddingValues(0.dp))
}
