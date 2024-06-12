package eu.virtusdevelops.rug_mobile.navigation

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector
import eu.virtusdevelops.rug_mobile.R
import java.util.UUID

sealed class Screen(val route: String, val icon: Int, val name: String) {
    data object MainScreen : Screen("main_screen", R.drawable.house_solid, "Home")
    data object PackageHoldersScreen : Screen("package_holders", R.drawable.house_solid, "Package Holders")
    data object AddPackageHolderScreen : Screen("add_package_holder", R.drawable.house_solid, "Add Package Holder")
    data object SettingsScreen : Screen("settings", R.drawable.gear_solid, "Settings")
    data object PackagesOutListScreen : Screen("packages_out", R.drawable.box_open_solid, "Outgoing packages")
    data object PackagesInListScreen : Screen("packages_in", R.drawable.box_solid, "Incoming packages")

    data object SendPackage : Screen("package_send", R.drawable.box_open_solid, "Send Package")
    data object PackageHolderScreen : Screen("package_holder_screen/{packageHolderID}", R.drawable.box_solid, "Package Holder") {
        fun createRoute(packageHolderID: Int) = "package_holder_screen/$packageHolderID"
    }

    data object IncomingPackageScreen : Screen("package_incoming/{packageID}", R.drawable.box_solid, "Package Details") {
        fun createRoute(packageID: UUID) = "package_incoming/$packageID"
    }


    data object OutgoingPackageScreen : Screen("package_outgoing/{packageID}", R.drawable.box_open_solid, "Package Details") {
        fun createRoute(packageID: UUID) = "package_outgoing/$packageID"
    }

}

sealed class AuthGraph(val route:String){
    data object LoginScreen : AuthGraph("login")
    data object FaceLoginScreen : AuthGraph("face_login")
    data object RegisterScreen : AuthGraph("register")
    data object ChangePasswordScreen : AuthGraph("change_password")
    data object SplashScreen : AuthGraph("splash")
    data object CameraPreviewScreen : AuthGraph("camera_preview")
    data object ActiveSessionScreen : AuthGraph("active_sessions")
    data object PendingSessionsScreen : AuthGraph("pending_sessions")
}