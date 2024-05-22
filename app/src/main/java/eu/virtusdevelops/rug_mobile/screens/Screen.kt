package eu.virtusdevelops.rug_mobile.screens

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object MainScreen : Screen("main_screen")
}