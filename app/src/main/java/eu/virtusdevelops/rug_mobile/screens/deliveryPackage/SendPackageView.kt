package eu.virtusdevelops.rug_mobile.screens.deliveryPackage

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.viewModels.OutgoingPackageListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.SendPackageViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

@Composable
fun SendPackageView(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    val viewModel = hiltViewModel<SendPackageViewModel>()
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var packageHolder by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var postNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    val packageHolderId by viewModel.internalPackageHolderId.collectAsState()
    val isBusy = viewModel.isBusy
    val isError = viewModel.isError.collectAsState()
    val error = viewModel.errorMessage.collectAsState()

    val modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()

    val scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        result.let {
            val qrData = result.toString()
            if (result is QRResult.QRUserCanceled) return@let
            Log.d("RESULT_DATA", qrData)
            packageHolder = (parseQrResult(qrData) ?: 0).toString()
        }
    }

    val context = LocalContext.current


    if (isError.value) {
        Toast.makeText(context, error.value, Toast.LENGTH_SHORT).show()
        viewModel.clearErrorMessage()
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .systemBarsPadding(),
    ) {
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
                    modifier = modifier,
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First name") },
                        modifier = modifier.weight(1f),
                        singleLine = true
                    )
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last name") },
                        modifier = modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    TextField(
                        value = packageHolder,
                        onValueChange = {
                            if(it.isDigitsOnly()){
                                packageHolder = it
                            }
                        },
                        label = { Text("Package holder ID") },
                        modifier = modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .align(Alignment.CenterVertically)
                    ) {
                        IconButton(
                            onClick = {
                                scanQRCodeLauncher.launch(null)
                            },
                            modifier = modifier
                                // .align(Alignment.CenterVertically)
                                .fillMaxHeight()
                                //.weight(0.2f)
                                .background(
                                    TextFieldDefaults.colors().unfocusedContainerColor,
                                    RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                                ),
                            content = {
                                Icon(
                                    modifier = Modifier.padding(10.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.qrcode_solid),
                                    contentDescription = "qr code scanner"
                                )
                            },
                        )
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp,
                            modifier = modifier
                                .align(Alignment.BottomCenter)
                        )
                    }
                }

                TextField(
                    value = streetAddress,
                    onValueChange = { streetAddress = it },
                    label = { Text("Street address") },
                    modifier = modifier,
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = houseNumber,
                        onValueChange = { houseNumber = it },
                        label = { Text("House number") },
                        modifier = modifier.weight(1f),
                        singleLine = true
                    )
                    TextField(
                        value = postNumber,
                        onValueChange = { postNumber = it },
                        label = { Text("Post number") },
                        modifier = modifier.weight(1f),
                        singleLine = true
                    )
                }

                TextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = modifier,
                    singleLine = true
                )

                TextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = modifier,
                    singleLine = true
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                val packageId by remember {
                    viewModel::packageId
                }

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

                            if (packageId != null)
                                navController.navigate(
                                    Screen.OutgoingPackageScreen.createRoute(
                                        packageId!!
                                    )
                                )

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
                    } else {
                        Text(
                            text = "Create package",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun parseQrResult(qrData: String): Int? {
    if (qrData.isEmpty()) return null
    val rawValueStartIndex = qrData.indexOf("rawValue=") + "rawValue=".length
    val rawValueEndIndex = qrData.indexOf(")", startIndex = rawValueStartIndex)
    val rawValue = qrData.substring(rawValueStartIndex, rawValueEndIndex)

    val components = rawValue.split(",")

    if (components.size == 3) {
        val url = components[0]
        val urlComponents = url.split("/")
        Log.d("QR_INFO", "URL: $url")
        if (urlComponents.size >= 11) {
            val packageHolderId = urlComponents[4].toIntOrNull() ?: 0
            Log.d("QR_INFO", "ID: $packageHolderId")
            return packageHolderId
        } else {
            Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
        }
    } else {
        Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
    }
    return null
}
