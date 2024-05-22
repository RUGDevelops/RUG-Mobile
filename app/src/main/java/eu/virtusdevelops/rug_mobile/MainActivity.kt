package eu.virtusdevelops.rug_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import eu.virtusdevelops.rug_mobile.navigation.SetupNavGraph
import eu.virtusdevelops.rug_mobile.ui.theme.RUGMobileTheme
import eu.virtusdevelops.rug_mobile.viewModels.LocalUserState
import eu.virtusdevelops.rug_mobile.viewModels.UserViewModel

class MainActivity : ComponentActivity() {
    private var history = mutableListOf<String>()
    private val userState by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            CompositionLocalProvider(LocalUserState provides userState) {


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

