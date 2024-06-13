package eu.virtusdevelops.rug_mobile.screens.deliveryPackage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatus
import eu.virtusdevelops.datalib.models.deliveryPackage.Recipient
import eu.virtusdevelops.rug_mobile.screens.home.baseStatusList
import eu.virtusdevelops.rug_mobile.viewModels.PackageViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun IncomingPackageView(navController: NavController,
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

                    IncomingPackageCard(deliveryPackage!!, viewModel)


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



@Composable
fun IncomingPackageCard(packageData: DeliveryPackage, viewModel: PackageViewModel) {

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

        PackageRecipientInformation(
            Recipient(
            packageData.recipientFirstName,
            packageData.recipientLastName,
            packageData.recipientEmail,
            packageData.street,
            packageData.houseNumber,
            packageData.city,
            packageData.postNumber,
            packageData.country
        )
        )


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
    }
}
