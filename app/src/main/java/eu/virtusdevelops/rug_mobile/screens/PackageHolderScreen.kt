package eu.virtusdevelops.rug_mobile.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderAction
import eu.virtusdevelops.datalib.models.PackageHolderActionStatus
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID





@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PackageHolderScreen(navController: NavController, packageHolderID: Int) {
    val viewModel =
        hiltViewModel<PackageHolderViewModel, PackageHolderViewModel.PackageHolderViewModelFactory>(
            creationCallback = { factory -> factory.create(packageHolderID = packageHolderID) }
        )

    val isBusy by viewModel::isBusy
    val isError by viewModel::isError

    LaunchedEffect(Unit) {
        if (!viewModel.isLoaded) {
            println("Fetching new data for model! ${viewModel.isLoaded}")
            viewModel.load()
        }
    }


    val refreshState = rememberPullRefreshState(
        refreshing = isBusy,
        onRefresh = { viewModel.load() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "History",
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
            if(isBusy){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }


            Row(
                modifier = Modifier.pullRefresh(refreshState)
            ){
                if (viewModel.packageHolder.value != null) {
                    // display packageholder
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)

                    ) {
                        PackageHolderInfo(viewModel.packageHolder.value!!)
                        PackageHolderHistory(viewModel.packageHolder.value!!)
                    }

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
fun PackageHolderHistory(packageHolder: PackageHolder) {

    if (packageHolder.history.isNotEmpty()) {
//            LazyColumn {
//                itemsIndexed(packageHolder.history) { _, item ->
//                    PackageHolderHistoryCard(item)
//                }
//            }

            LazyColumn {
                packageHolder.history.forEach { (date, actions) ->
                    item  {
                        PerDayHistoryCard(actions)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }



    } else {

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )

        ) {
            Text(
                text = "No history found",
                modifier = Modifier.padding(8.dp)
            )
        }

    }
}


@Composable
fun PerDayHistoryCard(history: List<PackageHolderAction>) {


    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    Text(
        text = dateFormat.format(history[0].date),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            history.forEach { action ->
                PackageHolderActionItem(action)
            }
        }

    }


}


@Composable
fun PackageHolderActionItem(action: PackageHolderAction) {

    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .padding(4.dp)
    ) {
        // Add icon based on action status
        Icon(
            imageVector = getActionIcon(action.status),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(46.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.status.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(text = dateFormat.format(action.date), fontSize = 12.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.secondary)
        }
    }
}


@Composable
fun getActionIcon(status: PackageHolderActionStatus): ImageVector {
    return when (status) {
        PackageHolderActionStatus.OPEN -> Icons.Default.Lock
        PackageHolderActionStatus.PACKAGE_TAKEN -> Icons.Default.CheckCircle
        PackageHolderActionStatus.DELIVER_PACKAGE -> Icons.Default.CheckCircle
        PackageHolderActionStatus.DEPOSIT_PACKAGE -> Icons.Default.CheckCircle
        PackageHolderActionStatus.PACKAGE_RECEIVED -> Icons.Default.CheckCircle
    }
}



@Composable
fun PackageHolderInfo(packageHolder: PackageHolder) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val lastModified = dateFormatter.format(packageHolder.lastModification)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
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




