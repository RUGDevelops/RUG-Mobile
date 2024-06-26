package eu.virtusdevelops.rug_mobile.screens.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.rug_mobile.modifiers.QrCodeButton
import eu.virtusdevelops.rug_mobile.viewModels.AddPackageHolderViewModel


@Composable
fun PackageHolderAddScreen(
    navController: NavController,
    innerPaddingValues: PaddingValues
) {
    var deviceID by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    val viewModel = hiltViewModel<AddPackageHolderViewModel>()

    val isError = viewModel.isError.collectAsState()

    val context = LocalContext.current
    if (isError.value) {
        Toast.makeText(context, "Invalid package holder", Toast.LENGTH_SHORT).show()
        viewModel.clearErrorMessage()
    }

    val modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    TextField(
                        value = deviceID.toString(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                deviceID = it
                            }
                        },
                        label = { Text("Device ID") },
                        placeholder = { Text("Enter device id") },
                        modifier = modifier
                            .weight(0.8f),
                        singleLine = true,
                        isError = deviceID.isEmpty(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Filled.Info, contentDescription = "Username")
                        })
                    QrCodeButton(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                        .align(Alignment.CenterVertically)) {
                        deviceID = it.toString()
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Button(
//                enabled = !isError,
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    viewModel.addPackageHolder(
                        deviceID.toInt(),
                        onSuccess = {
                            Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    )
                },
                enabled = deviceID.isNotEmpty(),
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
                    Text(
                        "Add",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }


}
