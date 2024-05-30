package eu.virtusdevelops.rug_mobile.screens.deliveryPackage

import android.media.MediaPlayer
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.User
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatus
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatusUpdate
import eu.virtusdevelops.datalib.models.deliveryPackage.Recipient
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.screens.home.ProgressBar
import eu.virtusdevelops.rug_mobile.screens.home.baseStatusList
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderViewModel
import eu.virtusdevelops.rug_mobile.viewModels.PackageViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipInputStream

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OutgoingPackageView(navController: NavController,
                        innerPaddingValues: PaddingValues,
                        deliveryPackageId: UUID
){


    val viewModel =
        hiltViewModel<PackageViewModel, PackageViewModel.PackageViewModelFactory>(
            creationCallback = { factory -> factory.create(packageID = deliveryPackageId) }
        )

    val deliveryPackage by viewModel.deliveryPackage.observeAsState(null)
    val isBusy by remember { viewModel::isBusy }
    val isError by remember { viewModel::isError }





    LaunchedEffect(Unit) {
        if(!viewModel.isLoaded)
            viewModel.load()
    }

    val refreshState = rememberPullRefreshState(
        refreshing = isBusy,
        onRefresh = { viewModel.load() }
    )


    Scaffold(
//        modifier = Modifier.padding(innerPaddingValues),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Package details",
                        fontWeight = FontWeight.Bold,)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon =  {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            if (isError) {
                Text(text = "An error occurred. Please try again.")
            }


            Row(
                modifier = Modifier
                    .pullRefresh(refreshState)
                    .fillMaxWidth()
            ){
                if (deliveryPackage != null) {

                    OutgoingPackageCard(deliveryPackage!!, viewModel)


                } else if(isError ){
                    Text(text = "Error occured, please try again")
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
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }
    }

}

data class DeliveryUpdate(val status: DeliveryPackageStatus, val date: Date?)

@Composable
fun OutgoingPackageCard(packageData: DeliveryPackage, viewModel: PackageViewModel) {

    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dynamicStatusList = packageData.statusUpdateList.map { it.status }

    val finalStatusList = baseStatusList.toMutableList().apply {
        dynamicStatusList.filter { it == DeliveryPackageStatus.ON_ROUTE }.forEach { status ->
            val index = indexOf(DeliveryPackageStatus.ON_ROUTE)
            add(index + 1, status)
        }
        if (packageData.delivered && !contains(DeliveryPackageStatus.DELIVERED)) {
            add(DeliveryPackageStatus.DELIVERED)
        }
    }

    val finalStatusUpdates = finalStatusList.map { status ->
        DeliveryUpdate(
            status = status,
            date = packageData.statusUpdateList.find { it.status == status }?.date
        )
    }


    Column (
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        DeliveryPackageDetailsCard(packageData)

        PackageSenderInformation(packageData.sender)

        PackageRecipientInformation(Recipient(
            packageData.recipientFirstName,
            packageData.recipientLastName,
            packageData.recipientEmail,
            packageData.street,
            packageData.houseNumber,
            packageData.city,
            packageData.postNumber,
            packageData.country
        ))


        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Updates",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ElevatedCard{
                VerticalProgressBar(
                    statusUpdates = finalStatusUpdates,
                    completedStatusList = dynamicStatusList
                )
            }
        }


        OutgoingPackageActions(packageData, viewModel)
    }
}

@Composable
fun DeliveryPackageDetailsCard(packageData: DeliveryPackage){
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val sentDate = dateFormatter.format(packageData.sentDate)
    val deliveryDate = packageData.estimatedDeliveryDate?.let { dateFormatter.format(it) } ?: "Not Available"
    ElevatedCard (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painterResource(id = R.drawable.box_solid),
                    modifier = Modifier.size(48.dp),
                    contentDescription = "Package Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${packageData.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sent Date: $sentDate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Estimated Delivery Date: $deliveryDate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Weight: ${packageData.weight} g",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if(packageData.deliveryPackageHolder != null){
                Text(
                    text = "Packageholder: ${packageData.deliveryPackageHolder!!.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun PackageSenderInformation(user: User){


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){

        Text(
            text = "Sender",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ElevatedCard{
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.user_solid),
                        modifier = Modifier.size(48.dp),
                        contentDescription = "Package Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.email,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
                Spacer(modifier = Modifier.height(2.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Firstname: ${user.firstname}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Lastname: ${user.lastname}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun PackageRecipientInformation(recipient: Recipient){


    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){

        Text(
            text = "Recipient",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ElevatedCard{
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.user_solid),
                        modifier = Modifier.size(48.dp),
                        contentDescription = "Package Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recipient.email,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
                Spacer(modifier = Modifier.height(2.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Firstname: ${recipient.firstname}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Lastname: ${recipient.lastname}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Street: ${recipient.street}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "House number: ${recipient.houseNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "City: ${recipient.city}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Post number: ${recipient.postNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Country: ${recipient.country}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VerticalProgressBar(statusUpdates: List<DeliveryUpdate>, completedStatusList: List<DeliveryPackageStatus>) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        statusUpdates.forEachIndexed { index, statusUpdate ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (statusUpdate.status in completedStatusList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (statusUpdate.status in completedStatusList) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White
                            )
                        }
                    }
                    if (index < statusUpdates.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(
                                    if (statusUpdate.status in completedStatusList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    )
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = statusUpdate.status.name,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                    if(statusUpdate.date != null){
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(statusUpdate.date),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start
                        )
                    }

                }
            }
        }
    }
}


@Composable
fun OutgoingPackageActions(packageData: DeliveryPackage, viewModel: PackageViewModel) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Text(
            text = "Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        val isDeposited =
            packageData.statusUpdateList.any { it.status == DeliveryPackageStatus.WAITING_IN_PACKAGE_HOLDER }
        val isPickedUp =
            packageData.statusUpdateList.any { it.status == DeliveryPackageStatus.PICKED_UP_BY_DELIVERY }


        val isBusy by viewModel::isBusy
        val isSoundError by viewModel::isOpenError
        val isVerifyError by viewModel::isVerifyError


        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ){

            if(!isDeposited){
                // give open button that plays sound to open package holder
                Button(
                    onClick = {
                        viewModel.openPackageHolder {
                            playBase64Wav(viewModel.openSound)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {

                    if(isBusy){
                        CircularProgressIndicator()
                    }else if (isSoundError){
                        Text(text = "Try again", fontWeight = FontWeight.Bold)
                    }else{
                        Text(text = "Open holder", fontWeight = FontWeight.Bold)
                    }

                }
                
                Spacer(modifier = Modifier.height(8.dp))

                // also give button to confirm deposit

                Button(
                    onClick = {
                        viewModel.verifySendPackage()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)

                ) {

                    if(isBusy){
                        CircularProgressIndicator()
                    }else if (isVerifyError){
                        Text(text = "Failed", fontWeight = FontWeight.Bold)
                    }else{
                        Text(text = "Verify Package", fontWeight = FontWeight.Bold)
                    }

                }


            }

            if(!isPickedUp && isDeposited){
                // display cancel delivery button
            }

        }

    }
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
    mediaPlayer.prepareAsync()
    mediaPlayer.setOnPreparedListener {
        it.start()
    }
}
