package eu.virtusdevelops.rug_mobile.screens.home

import android.media.MediaPlayer
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderStatus
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.screens.GradientCard
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderListViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.ZipInputStream


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackageHoldersScreen(
    navController: NavController,
    innerPaddingValues: PaddingValues
) {


    val packageHolderViewModel = hiltViewModel<PackageHolderListViewModel>()
    val packageHolders by packageHolderViewModel.packageHolders.observeAsState(emptyList())
    val isBusy by remember { packageHolderViewModel::isBusy }
    val isError by remember { packageHolderViewModel::isError }




    // Load data when the composable is first composed
    LaunchedEffect(Unit) {
        if(!packageHolderViewModel.isLoaded)
            packageHolderViewModel.load()
    }


    val refreshState = rememberPullRefreshState(
        refreshing = isBusy,
        onRefresh = { packageHolderViewModel.load() }
    )


    Box(
        modifier = Modifier
            .pullRefresh(refreshState)
            .padding(innerPaddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isBusy) {
                CircularProgressIndicator()
            } else if (isError) {
                Text(text = "An error occurred. Please try again.")
            } else {
                if (packageHolders.isEmpty()) {
                    Text(text = "No package holders found.")
                } else {
                    ListOfPackageHolders(navController, packageHolderViewModel, packageHolders)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pullRefresh(refreshState),
        contentAlignment = Alignment.TopCenter
    ) {

        PullRefreshIndicator(
            refreshing = isBusy,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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


    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onDetailsClick
    ) {
        Box (
            modifier = Modifier
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surfaceContainer
                        ),
                        center = Offset(
                            x = screenWidthPx / 1.1f,
                            y = -100f
                        ),
                        radius = screenWidthPx
                    )
                )
        ){
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

