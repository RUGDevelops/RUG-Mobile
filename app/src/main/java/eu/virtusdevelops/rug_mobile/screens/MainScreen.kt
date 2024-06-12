package eu.virtusdevelops.rug_mobile.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.AuthGraph
import eu.virtusdevelops.rug_mobile.navigation.Graph
import eu.virtusdevelops.rug_mobile.navigation.Screen
import eu.virtusdevelops.rug_mobile.navigation.SetupNavGraph
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    // maybe convert main screen to use nested navigation, to seperate packageholders screen and packages screen?


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




    Scaffold(
        topBar = {
            TopNavBar(navController, viewModel)
        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { presses++ }) {
//                Icon(Icons.Default.Add, contentDescription = "Send")
//            }
//        }
        bottomBar = {
            BottomBar(navController = navController)
        }
        // setup bottom bar here
    ) { innerPadding ->
        SetupNavGraph(navController = navController, innerPadding = innerPadding)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
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
    if (bottomBarDestination) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavHostController, viewModel: UserViewModel){

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
    }else if(currentDestination?.route == Screen.SettingsScreen.route){
        CustomTopbar("Settings", navController)
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