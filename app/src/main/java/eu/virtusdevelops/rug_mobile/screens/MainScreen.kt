package eu.virtusdevelops.rug_mobile.screens

import android.media.MediaPlayer
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.R
import eu.virtusdevelops.rug_mobile.modifiers.FloatingQRButton
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.navigation.SetupNavGraph
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.PackageScanViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {


    val viewModel = hiltViewModel<PackageScanViewModel>()



    Scaffold(
        topBar = {
            TopNavBar(navController)
        },
        floatingActionButton = {
            FloatingBar(navController = navController, viewModel)
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
        // setup bottom bar here
    ) { innerPadding ->
        SetupNavGraph(navController = navController, innerPadding = innerPadding)
    }
}



@Composable
fun FloatingBar(navController: NavController, viewModel: PackageScanViewModel){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isBusy by remember {
        viewModel::isBusy
    }


    val isError = viewModel.isError.collectAsState()
    val context = LocalContext.current

    if (isError.value) {
        Toast.makeText(context, viewModel.errorMessage.collectAsState().value, Toast.LENGTH_SHORT).show()
        viewModel.clearErrorMessage()
    }



    if(currentDestination?.route == Screen.PackagesOutListScreen.route){
        androidx.compose.material3.FloatingActionButton(
            onClick = {
                navController.navigate(Screen.SendPackage.route)
            },
        ) {
            Icon(Icons.Default.Add, contentDescription = "Send")
        }
    }else if(currentDestination?.route == Screen.PackageHoldersScreen.route){
        androidx.compose.material3.FloatingActionButton(
            onClick = {
                navController.navigate(Screen.AddPackageHolderScreen.route)
            },
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }else if(currentDestination?.route == Screen.PackagesInListScreen.route){
        if(isBusy){

            androidx.compose.material3.FloatingActionButton(
                onClick = {

                },
            ) {
                CircularProgressIndicator()
            }


        }else{
            FloatingQRButton(modifier = Modifier) {
                viewModel.claimPackage(it) {sound ->

                    // play sound
                    playBase64Wav(sound)
                }
            }
        }

    }

}


@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        Screen.PackageHoldersScreen,
        Screen.PackagesOutListScreen,
        Screen.PackagesInListScreen
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }


    if(bottomBarDestination){
        NavigationBar {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }

//    AnimatedVisibility(
//        visible = bottomBarDestination,
//        enter = slideInVertically(),
//        exit = slideOutVertically()
//    ) {
//        NavigationBar {
//            screens.forEach { screen ->
//                AddItem(
//                    screen = screen,
//                    currentDestination = currentDestination,
//                    navController = navController
//                )
//            }
//        }
//
//    }


}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavHostController){

    val screens = listOf(
        Screen.PackageHoldersScreen,
        Screen.PackagesOutListScreen,
        Screen.PackagesInListScreen,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }

    if(currentDestination?.route == Screen.SendPackage.route){
        CustomTopbar("Send package", navController)
    }else if(currentDestination?.route == Screen.AddPackageHolderScreen.route){
        CustomTopbar("Add Package Holder", navController)
    }else if(currentDestination?.route == Screen.SettingsScreen.route){
        CustomTopbar("Settings", navController)
    }else if(currentDestination?.route == Screen.PackageHolderScreen.route){
        CustomTopbar("History", navController)
    }else if(currentDestination?.route == AuthGraph.PendingSessionsScreen.route){
        CustomTopbar("Pending sessions", navController)
    }else if(currentDestination?.route == AuthGraph.ActiveSessionScreen.route){
        CustomTopbar("Active sessions", navController)
    }
    else {
        if (bottomBarDestination) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(screens.find { it.route == currentDestination?.route }?.name ?: "Unknown")
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.SettingsScreen.route)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    }



}


@Composable
fun RowScope.AddItem(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    NavigationBarItem(label = {
        Text(text = screen.name)
    }, icon = {
        Icon(
            painterResource(id = screen.icon),
            modifier = Modifier.size(32.dp),
            contentDescription = "Navigation Icon"
        )
    }, selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true, onClick = {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.id)
            launchSingleTop = true
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopbar(title: String, navController: NavController){
    TopAppBar(
        title = {
            Text(text = title,
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


@Preview
@Composable
fun MainScreenPreview() {
    RUGMobileTheme {
        MainScreen(navController = rememberNavController())
    }
}