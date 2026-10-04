package apw.android.hubspace.utils

sealed class Screen(
    val route: String
) {
    object Welcome: Screen("welcome")
    object Home: Screen("home")
    object AdminPanel: Screen("admin_panel")
    object Unauthorized: Screen("unauthorized")
}