package apw.android.hubspace.utils

sealed class Screen(
    val route: String
) {
    object Welcome: Screen("welcome")
    object Home: Screen("home")
    object Unauthorized: Screen("unauthorized")
}