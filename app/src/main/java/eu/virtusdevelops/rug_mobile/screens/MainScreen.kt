package eu.virtusdevelops.rug_mobile.screens

import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderStatus
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderViewModel
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipInputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MainScreen(
    navController: NavController
) {
//    var viewModel = LocalUserState.current
    var presses by remember { mutableIntStateOf(0) }
    val scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        result.let {
            var qrData = result.toString()
            if(result is QRResult.QRUserCanceled) return@let

            Log.d("RESULT_DATA", qrData)
            parseQrResult(qrData)
        }
    }

    val viewModel = hiltViewModel<UserViewModel>()
    val packageHolderViewModel = hiltViewModel<PackageHolderListViewModel>()
    val packageHolders by packageHolderViewModel.packageHolders.observeAsState(emptyList())
    val isBusy by remember { packageHolderViewModel::isBusy }
    val isError by remember { packageHolderViewModel::isError }

    // Load data when the composable is first composed
    LaunchedEffect(Unit) {
        if(!packageHolderViewModel.isLoaded)
            packageHolderViewModel.load()
    }

    if (!viewModel.isLoggedIn && !viewModel.isBusy) {
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(navController.graph.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Package holders")
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout(onSuccess = {
                            navController.navigate(Screen.SplashScreen.route) {
                                popUpTo(navController.graph.id)
                            }
                        })
                        if (!viewModel.isLoggedIn) {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(navController.graph.id)
                            }
                        }
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Send")
            }
        }
    ) { innerPadding ->

        val refreshState = rememberPullRefreshState(
            refreshing = isBusy,
            onRefresh = { packageHolderViewModel.load() }
        )

        Box(
            modifier = Modifier.pullRefresh(refreshState)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isBusy) {
                    CircularProgressIndicator()
                } else if (isError) {
                    Text(text = "An error occurred. Please try again.")
                } else {
                    ListOfPackageHolders(navController, packageHolderViewModel, packageHolders)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pullRefresh(refreshState),
            contentAlignment = Alignment.TopCenter){

            PullRefreshIndicator(
                refreshing = isBusy,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }



    }
}


@Composable
fun ListOfPackageHolders(navController: NavController, packageHolderViewModel: PackageHolderListViewModel, packageHolders : List<PackageHolder>){

    LazyColumn {

        items(packageHolders) { packageHolder ->
            val openSound by remember { packageHolderViewModel::openSound }
            val isLoadingSound by packageHolderViewModel::isLoadingSound

            PackageHolderBar(
                packageHolder = packageHolder,
                onOpenClick = {
                    packageHolderViewModel.getOpenSound(packageHolder.id) {
                        if (openSound.isNotEmpty())
                            playBase64Wav(openSound)
                    }
                },
                onDetailsClick = {
                    if(!isLoadingSound)
                        navController.navigate(Screen.PackageHolderScreen.createRoute(packageHolder.id))
                },
                isLoadingSound
            )
        }
    }
}

@Composable
fun PackageHolderBar(packageHolder: PackageHolder, onOpenClick: () -> Unit, onDetailsClick: () -> Unit, isLoading: Boolean) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val lastModified = dateFormatter.format(packageHolder.lastModification)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        onClick = onDetailsClick
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "ID: ${packageHolder.id}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last Modified: $lastModified",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusMessage(packageHolder.status)
                }

                // Open button
                IconButton(
                    onClick = onOpenClick,
                    modifier = Modifier
                        .size(24.dp)
                ) {
                    if(!isLoading){
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Open package holder",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }else{

                        CircularProgressIndicator()

//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = "Open package holder",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
                    }

                }
            }

            // Status circle indicator
            AnimatedStatusIndicator(
                status = packageHolder.status,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp) // Adjust to position inside card padding
            )
        }
    }
}


@Composable
fun StatusMessage(status: PackageHolderStatus, modifier: Modifier = Modifier){
    val message = when (status) {
        PackageHolderStatus.EMPTY -> "Currently empty"
        PackageHolderStatus.HOLDING_RECEIVED_PACKAGE -> "Holding your delivered package"
        PackageHolderStatus.WAITING_PACKAGE_DEPOSIT -> "Waiting for package deposit"
        PackageHolderStatus.WAITING_PACKAGE_INSERT -> "Waiting for package to be inserted"
        PackageHolderStatus.HOLDING_TO_SEND_PACKAGE -> "Waiting for delivery to pickup package"
    }


    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}


@Composable
fun AnimatedStatusIndicator(status: PackageHolderStatus, modifier: Modifier = Modifier) {
    val targetColor = when (status) {
        PackageHolderStatus.EMPTY, PackageHolderStatus.HOLDING_RECEIVED_PACKAGE -> Color.Green
        PackageHolderStatus.WAITING_PACKAGE_DEPOSIT, PackageHolderStatus.WAITING_PACKAGE_INSERT -> Color(252, 140, 3)
        else -> Color.Red
    }
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(targetColor)
            .border(1.dp, targetColor.copy(alpha = 0.9f), CircleShape)
            .shadow(9.dp, CircleShape)

    )
}

private fun playBase64Wav(base64Wav: String) {
    // Decode the Base64 string to a byte array
    val decodedWav = Base64.decode(base64Wav, Base64.DEFAULT)

    // Write the byte array to a temporary .zip file
    val tempZipFile = File.createTempFile("temp", ".zip")
    val fos = FileOutputStream(tempZipFile)
    fos.write(decodedWav)
    fos.close()

    // Unzip the .zip file to extract the .wav file
    val zis = ZipInputStream(tempZipFile.inputStream())
    val entry = zis.nextEntry
    val tempWavFile = File.createTempFile("temp", ".wav")
    tempWavFile.outputStream().use { it.write(zis.readBytes()) }

    // Use a MediaPlayer to play the .wav file
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(tempWavFile.absolutePath)
    mediaPlayer.prepare()
    mediaPlayer.start()
}



private fun parseQrResult(qrData: String) {
    if(qrData.isEmpty()) return
    val rawValueStartIndex = qrData.indexOf("rawValue=") + "rawValue=".length
    val rawValueEndIndex = qrData.indexOf(")", startIndex = rawValueStartIndex)
    val rawValue = qrData.substring(rawValueStartIndex, rawValueEndIndex)

    val components = rawValue.split(",")

    if (components.size == 3) {
        val url = components[0]
        val urlComponents = url.split("/")

        if (urlComponents.size >= 11) {
            val deliveryId = urlComponents[3].toIntOrNull() ?: 0
            val boxId = urlComponents[4].toIntOrNull() ?: 0
            val tokenFormat = urlComponents[5].toIntOrNull() ?: 0
            val latitude = urlComponents[6].toIntOrNull() ?: 0
            val longitude = urlComponents[7].toIntOrNull() ?: 0
            val terminalSeed = urlComponents[8].toIntOrNull() ?: 0
            val doorIndex = urlComponents[9].toIntOrNull() ?: 0

            for (component in urlComponents) {
                Log.d("URL_COMPONENT", component)
            }

//            makeApiRequest(
//                Box(
//                    deliveryId = deliveryId,
//                    boxId = boxId.toString().trimStart('0').toInt(),
//                    tokenFormat = 2,
//                    latitude = latitude,
//                    longitude = longitude,
//                    qrCodeInfo = "string",
//                    terminalSeed = terminalSeed,
//                    isMultibox = false,
//                    doorIndex = doorIndex,
//                    addAccessLog = true
//                )
//            )
        } else {
            Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
        }
    } else {
        Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    RUGMobileTheme {
        MainScreen(navController = rememberNavController())
    }
}