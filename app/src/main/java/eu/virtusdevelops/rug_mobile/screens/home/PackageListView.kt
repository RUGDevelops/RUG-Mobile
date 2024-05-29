package eu.virtusdevelops.rug_mobile.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatus
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatusUpdate
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.PackageListViewModel
import java.util.Date


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackageListView(navController: NavController, innerPaddingValues: PaddingValues)
{

    val packageHolderViewModel = hiltViewModel<PackageListViewModel>()
    val deliveryPackages by packageHolderViewModel.deliveryPackages.observeAsState(emptyList())
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
    ){
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
                if(deliveryPackages.size == 0){
                    Text(text = "No packages found.")
                }else{
                    ListAllPackages(packages = deliveryPackages)
                }
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


@Composable
fun ListAllPackages(packages: List<DeliveryPackage>){
    LazyColumn {
        packages.forEach {
            item{
                DeliveryPackageCard(it)
            }
        }
    }
}



@Composable
fun DeliveryPackageCard(packageData: DeliveryPackage) {
    val statusList = packageData.statusUpdateList.map { it.status }.toMutableList()
//    if (packageData.delivered) {
//        statusList.add("DELIVERED")
//    }
    val baseStatusList = listOf(
        DeliveryPackageStatus.CREATED,
        DeliveryPackageStatus.WAITING_IN_PACKAGE_HOLDER,
        DeliveryPackageStatus.PICKED_UP_BY_DELIVERY,
        DeliveryPackageStatus.ON_ROUTE,
        DeliveryPackageStatus.DELIVERED,
        DeliveryPackageStatus.CLAIMED
    )

    val dynamicStatusList = packageData.statusUpdateList.map { it.status }
    val finalStatusList = baseStatusList.toMutableList()

    dynamicStatusList.filter { it == DeliveryPackageStatus.ON_ROUTE }.forEach { status ->
        val index = finalStatusList.indexOf(DeliveryPackageStatus.ON_ROUTE)
        finalStatusList.add(index + 1, status)
    }

    if (packageData.delivered && !finalStatusList.contains(DeliveryPackageStatus.DELIVERED)) {
        finalStatusList.add(DeliveryPackageStatus.DELIVERED)
    }


    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Package ID: ${packageData.id}", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("Recipient: ${packageData.recipientFirstName} ${packageData.recipientLastName}", fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
            Text("Sent Date: ${packageData.sentDate}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Estimated Delivery Date: ${packageData.estimatedDeliveryDate ?: "Not Available"}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Weight: ${packageData.weight} g", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            ProgressBar(statusList = finalStatusList, completedStatusList = dynamicStatusList)
        }
    }
}
@Composable
fun ProgressBar(statusList: List<DeliveryPackageStatus>, completedStatusList: List<DeliveryPackageStatus>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            statusList.forEach { status ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(2.dp))
//                    Text(text = status.name, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in statusList.indices) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (statusList[i] in completedStatusList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                )
                if (i < statusList.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .height(3.dp)
                            .weight(1f)
                            .background(if (statusList[i] in completedStatusList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}
