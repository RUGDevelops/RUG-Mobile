package eu.virtusdevelops.rug_mobile.screens.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.screens.GradientBackground
import eu.virtusdevelops.rug_mobile.screens.deliveryPackage.parseQrResult
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode


@Composable
fun PackageHolderAddScreen(
    navController: NavController,
) {
    var deviceID by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

     val viewModel = hiltViewModel<PackageHolderListViewModel>()

    val scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        result.let {
            var qrData = result.toString()
            if(result is QRResult.QRUserCanceled) return@let

            Log.d("RESULT_DATA", qrData)
            var packageHolderId = parseQrResult(qrData) ?: 0
        }
    }


    val modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp)

        GradientBackground{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ADD PACKAGE\nHOLDER", modifier = Modifier
                            .padding(5.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = (modifier.fillMaxSize(0.1f)))


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            TextField(
                                value = deviceID,
                                onValueChange = { deviceID = it },
                                label = { Text("Device ID") },
                                placeholder = { Text("Enter device id") },
                                modifier = modifier
                                    .weight(0.8f),
                                singleLine = true,
                                isError = viewModel.isError != null,
                                leadingIcon = {
                                    Icon(Icons.Filled.Info, contentDescription = "Username")
                                })
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
                                        .fillMaxHeight()
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
                                TabRowDefaults.Divider(
                                    color = Color.Black,
                                    thickness = 1.dp,
                                    modifier = modifier
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
                Column {
                    Button(
//                enabled = !isError,
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
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
                                "Add",
                                fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
//                enabled = !isError,
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
                            "Cancel",
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

}

@Preview
@Composable
fun PackageHolderAddScreenPreview() {
    PackageHolderAddScreen(navController = rememberNavController())
}