package eu.virtusdevelops.rug_mobile

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.SetupNavGraph
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.LocalPackageHolderListState
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState
import eu.virtusdevelops.rug_mobile.viewModels.PackageHolderListViewModel
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel

class MainActivity : ComponentActivity() {
    private val userState: UserViewModel by viewModels { UserViewModelFactory(application) }
    private val packageHolderViewModel: PackageHolderListViewModel by viewModels { PackageHolderViewModelFactory(application) }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalUserState provides userState,
                LocalPackageHolderListState provides packageHolderViewModel
            ) {
                RUGMobileTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        SetupNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

class PackageHolderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PackageHolderListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PackageHolderListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class UserViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

