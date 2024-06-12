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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import eu.virtusdevelops.rug_mobile.screens.GradientCard
import eu.virtusdevelops.rug_mobile.viewModels.OutgoingPackageListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OutgoingPackageListView(navController: NavController, innerPaddingValues: PaddingValues) {

    val packagesListViewModel = hiltViewModel<OutgoingPackageListViewModel>()
    val deliveryPackages by packagesListViewModel.deliveryPackages.observeAsState(emptyList())
    val isBusy by remember { packagesListViewModel::isBusy }
    val isError by remember { packagesListViewModel::isError }


    // Load data when the composable is first composed
    LaunchedEffect(Unit) {
        if (!packagesListViewModel.isLoaded)
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
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isError) {
                Text(text = "An error occurred. Please try again.")
            } else if (!isBusy) {
                if (deliveryPackages.isEmpty()) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "No packages found.")
                    }

                } else {
                    ListAllOutgoingPackages(packages = deliveryPackages, navController)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPaddingValues)
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
fun ListAllOutgoingPackages(packages: List<DeliveryPackage>, navController: NavController) {
    LazyColumn {
        packages.forEach {
            item {
                OutgoingDeliveryPackageCard(it) {
                    navController.navigate(Screen.OutgoingPackageScreen.createRoute(it.id))
                }
            }
        }
    }
}


@Composable
fun OutgoingDeliveryPackageCard(packageData: DeliveryPackage, onClick: () -> Unit) {

    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val sentDate = dateFormatter.format(packageData.sentDate)
    val deliveryDate =
        packageData.estimatedDeliveryDate?.let { dateFormatter.format(it) } ?: "Not Available"


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

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    GradientCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        onClick = onClick,


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
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recipient: ${packageData.recipientFirstName} ${packageData.recipientLastName}",
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

            if (packageData.deliveryPackageHolder != null) {
                Text(
                    text = "Packageholder: ${packageData.deliveryPackageHolder!!.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(16.dp))
            ProgressBar(
                statusList = finalStatusList,
                completedStatusList = dynamicStatusList
            )
        }
    }

}




