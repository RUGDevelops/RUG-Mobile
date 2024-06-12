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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackage
import eu.virtusdevelops.datalib.models.deliveryPackage.DeliveryPackageStatus
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.viewModels.IncomingPackageListViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IncomingPackageListView(navController: NavController, innerPaddingValues: PaddingValues)
{

    val packagesListViewModel = hiltViewModel<IncomingPackageListViewModel>()
    val deliveryPackages by packagesListViewModel.deliveryPackages.observeAsState(emptyList())
    val isBusy by remember { packagesListViewModel::isBusy }
    val isError by remember { packagesListViewModel::isError }


    // Load data when the composable is first composed
    LaunchedEffect(Unit) {
        if(!packagesListViewModel.isLoaded)
            packagesListViewModel.load()
    }



    val refreshState = rememberPullRefreshState(
        refreshing = isBusy,
        onRefresh = { packagesListViewModel.load() }
    )



    Box(
        modifier = Modifier
            .pullRefresh(refreshState)
            .padding(innerPaddingValues)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isError) {
                Text(text = "An error occurred. Please try again.")
            } else if(!isBusy){
                if(deliveryPackages.isEmpty()){
                    Text(text = "No packages found.")
                }else{
                    ListAllPackages(packages = deliveryPackages, navController)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPaddingValues)
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
fun ListAllPackages(packages: List<DeliveryPackage>, navController: NavController){
    LazyColumn {
        packages.forEach {
            item{
                IncomingDeliveryPackageCard(it) {
                    navController.navigate(Screen.IncomingPackageScreen.createRoute(it.id))
                }
            }
        }
    }
}


val baseStatusList = listOf(
    DeliveryPackageStatus.CREATED,
    DeliveryPackageStatus.WAITING_IN_PACKAGE_HOLDER,
    DeliveryPackageStatus.PICKED_UP_BY_DELIVERY,
    DeliveryPackageStatus.ON_ROUTE,
    DeliveryPackageStatus.DELIVERED,
    DeliveryPackageStatus.CLAIMED
)


@Composable
fun IncomingDeliveryPackageCard(packageData: DeliveryPackage, onClick: () -> Unit) {

    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val sentDate = dateFormatter.format(packageData.sentDate)
    val deliveryDate = packageData.estimatedDeliveryDate?.let { dateFormatter.format(it) } ?: "Not Available"


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

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
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
                text = "Sender: ${packageData.sender.firstname} ${packageData.sender.lastname}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
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


            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(16.dp))
            ProgressBar(
                statusList = finalStatusList,
                completedStatusList = dynamicStatusList
            )
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
