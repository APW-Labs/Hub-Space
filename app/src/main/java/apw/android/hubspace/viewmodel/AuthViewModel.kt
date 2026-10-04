package apw.android.hubspace.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apw.android.hubspace.auth.AuthRepository
import apw.android.hubspace.auth.AuthResult
import apw.android.hubspace.auth.signInWithGoogle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle: AuthUiState()
    object Loading: AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Unauthorized(val email: String): AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onGoogleSignInClicked(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val credential = signInWithGoogle(
                context = context,
                webClientId = webClientId
            )

            if (credential == null) {
                _uiState.value = AuthUiState.Error("Google Sign-In canceled or failed")
                return@launch
            } else {
                when (val result = repository.authenticateAndCheckWhitelist(credential.idToken)) {
                    is AuthResult.Success -> {
                        _uiState.value = AuthUiState.Success(result.email)
                    }
                    is AuthResult.Unauthorized -> {
                        _uiState.value = AuthUiState.Unauthorized(result.message)
                    }
                    is AuthResult.Failed -> {
                        _uiState.value = AuthUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun checkSessionOnStart() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val currentUser = repository.auth.currentUser
            if (currentUser == null) {
                _uiState.value = AuthUiState.Idle
                return@launch
            }

            when (val result = repository.checkStatus()) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState.Success(result.email)
                }
                is AuthResult.Unauthorized -> {
                    _uiState.value = AuthUiState.Unauthorized(currentUser.email ?: "")
                }
                is AuthResult.Failed -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    fun recheckWhitelistStatus() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            when (val result = repository.checkStatus()) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState.Success(result.email)
                }
                is AuthResult.Unauthorized -> {
                    _uiState.value = AuthUiState.Error("Still not whitelisted. Contact admin if already added.")
                }
                is AuthResult.Failed -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}