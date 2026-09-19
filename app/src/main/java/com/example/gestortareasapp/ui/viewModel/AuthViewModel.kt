package com.example.gestortareasapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareasapp.domain.usecase.auth.LoginUserUseCase
import com.example.gestortareasapp.domain.usecase.auth.LogoutUserUseCase
import com.example.gestortareasapp.domain.usecase.auth.RegisterUserUseCase
import com.example.gestortareasapp.ui.state.OperationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val operationState: OperationState = OperationState.Idle
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun login(email: String, password: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationState = OperationState.Loading) }
            val result = loginUserUseCase(email, password)

            result.onSuccess {
                _uiState.update { it.copy(operationState = OperationState.Success) }
                onComplete()
            }.onFailure { error ->
                _uiState.update { it.copy(operationState = OperationState.Error(error.message ?: "Error al iniciar sesión")) }
            }
        }
    }

    fun register(email: String, password: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationState = OperationState.Loading) }
            val result = registerUserUseCase(email, password)

            result.onSuccess {
                _uiState.update { it.copy(operationState = OperationState.Success) }
                onComplete()
            }.onFailure { error ->
                _uiState.update { it.copy(operationState = OperationState.Error(error.message ?: "Error al registrarse")) }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            logoutUserUseCase()
            _uiState.update { AuthUiState() } // Reset to Idle
            onComplete()
        }
    }

    fun clearError() {
        if (_uiState.value.operationState is OperationState.Error) {
            _uiState.update { it.copy(operationState = OperationState.Idle) }
        }
    }
}
