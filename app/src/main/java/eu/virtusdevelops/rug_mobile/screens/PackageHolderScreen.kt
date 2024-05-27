package eu.virtusdevelops.rug_mobile.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import eu.virtusdevelops.datalib.models.PackageHolder
import eu.virtusdevelops.datalib.models.PackageHolderAction
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


@Composable
fun PackageHolderScreen(navController: NavController, packageHolderID: Int) {

//    val assistedFactory: PackageHolderViewModel.PackageHolderViewModelFactory = hiltViewModel() // Obtain the Hilt ViewModel to get the factory
//
//    val viewModel: PackageHolderViewModel = viewModel(
//        factory = PackageHolderViewModel.provideFactory(assistedFactory, packageHolderID)
//    )

    val viewModel = hiltViewModel<PackageHolderViewModel, PackageHolderViewModel.PackageHolderViewModelFactory>(
        creationCallback = { factory -> factory.create(packageHolderID = packageHolderID) }
    )


    val isBusy by viewModel::isBusy

    LaunchedEffect(Unit) {
        if(!viewModel.isLoaded){
            println("Fetching new data for model! ${viewModel.isLoaded}")
            viewModel.load()
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isBusy -> {
                CircularProgressIndicator()
            }
            else -> {
                if(viewModel.packageHolder.value != null){
                    // display packageholder
                    Column {
                        PackageHolderInfo(viewModel.packageHolder.value!!)
                        PackageHolderHistory(viewModel.packageHolder.value!!)
                    }
                }else{
                    Text(text = "Error occured, please try again")
                }
            }
        }
    }
}


@Composable
fun PackageHolderHistory(packageHolder: PackageHolder){
    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        if(packageHolder.history != null){
            LazyColumn {
                itemsIndexed(packageHolder.history) { _, item ->
                    PackageHolderHistoryCard(item)
                }
            }
        } else {
            Text(
                text = "Failed loading package holder history",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}



@Composable
fun PackageHolderHistoryCard(action: PackageHolderAction){
    Text(text = action.action)
}

@Composable
fun PackageHolderInfo(packageHolder: PackageHolder) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val lastModified = dateFormatter.format(packageHolder.lastModification)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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




