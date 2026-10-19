package apw.android.hubspace.utils

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import apw.android.hubspace.WelcomeRoute
import apw.android.hubspace.ui.screens.MainScreen
import apw.android.hubspace.ui.screens.UnauthorizedScreen
import apw.android.hubspace.viewmodel.AuthUiState
import apw.android.hubspace.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HubSpaceNavHost(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.checkSessionOnStart()
    }

    LoadingGate(
        isLoading = uiState is AuthUiState.Loading,
        messages = listOf(
            "Verifying your workspace credentials...",
            "Securing your connection...",
            "Encrypting session keys...",
            "Synchronizing workspace access..."
        )
    ) {

        when (uiState) {
            is AuthUiState.Loading -> {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
            }

            else -> {
                val startDestination = when (uiState) {
                    is AuthUiState.Success -> Screen.Home.route
                    is AuthUiState.Unauthorized -> Screen.Unauthorized.route
                    else -> Screen.Welcome.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(Screen.Welcome.route) {
                        WelcomeRoute(
                            onNavigateToHome = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            },
                            onNavigateToUnauthorized = {
                                navController.navigate(Screen.Unauthorized.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Home.route) {
                        MainScreen(
                            email = auth.currentUser?.email ?: "",
                            onSignOutClicked = {
                                auth.signOut()
                                navController.navigate(Screen.Welcome.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            },
                            viewModel = viewModel,
                            onTabSelected = { route ->

                            }
                        )
                    }
                    composable(Screen.Unauthorized.route) {
                        UnauthorizedScreen(
                            userEmail = auth.currentUser?.email ?: "null",
                            onRetryClick = {
                                viewModel.recheckWhitelistStatus()
                            },
                            onRequestAccessClick = {
                                val intent: Intent = Intent()
                                intent.action = Intent.ACTION_VIEW
                                intent.data = "https://wa.me/917404155671".toUri()

                                context.startActivity(intent)
                            }
                        ) {
                            auth.signOut()
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
        }
    }
}