package eu.virtusdevelops.rug_mobile.screens

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object MainScreen : Screen("main_screen")
    data object SplashScreen : Screen("splash_screen")
    data object RegisterScreen : Screen("register_screen")
//    data object PackageHolderScreen : Screen("package_holder_screen")
    data object PackageHolderScreen : Screen("package_holder_screen/{packageHolderID}") {
        fun createRoute(packageHolderID: Int) = "package_holder_screen/$packageHolderID"
    }
}