package apw.android.hubspace

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import apw.android.hubspace.ui.screens.WelcomeScreen
import apw.android.hubspace.ui.theme.HubSpaceTheme
import apw.android.hubspace.utils.HubSpaceNavHost
import apw.android.hubspace.viewmodel.AuthUiState
import apw.android.hubspace.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            HubSpaceTheme {
                HubSpaceNavHost()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    HubSpaceTheme {
        Greeting("Android")
    }
}

@Composable
fun WelcomeRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToUnauthorized: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val clientId = stringResource(R.string.web_client_id)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "Welcome ${state.userId}!", Toast.LENGTH_LONG).show()
                onNavigateToHome()
            }
            is AuthUiState.Unauthorized -> {
                onNavigateToUnauthorized()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                ((context) as? Activity)?.finish()
            }
            else -> {}
        }
    }

    WelcomeScreen(
        isLoading = uiState is AuthUiState.Loading,
        onGoogleSignInClick = {
            viewModel.onGoogleSignInClicked(context, clientId)
        }
    )
}