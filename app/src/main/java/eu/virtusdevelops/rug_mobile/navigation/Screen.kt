package eu.virtusdevelops.rug_mobile.navigation

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val name: String) {
//    data object LoginScreen : Screen("login_screen")
    data object MainScreen : Screen("main_screen", Icons.Default.Lock, "Home")
//    data object SplashScreen : Screen("splash_screen")
//    data object RegisterScreen : Screen("register_screen")
    data object PackageHoldersScreen : Screen("package_holders", Icons.Default.Lock, "Package Holders")
//    data object PackageHolderScreen : Screen("package_holder_screen")

    data object PackagesListScreen : Screen("packages", Icons.Default.Lock, "Packages")
    data object PackageHolderScreen : Screen("package_holder_screen/{packageHolderID}", Icons.Default.Lock, "Package") {
        fun createRoute(packageHolderID: Int) = "package_holder_screen/$packageHolderID"
    }
}

sealed class AuthGraph(val route:String){
    data object LoginScreen : AuthGraph("login")
    data object RegisterScreen : AuthGraph("register")
    data object SplashScreen : AuthGraph("splash")
}