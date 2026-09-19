package com.example.gestortareasapp.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.gestortareasapp.ui.state.OperationState
import com.example.gestortareasapp.ui.viewModel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiStateFlow = authViewModel.uiState.collectAsState()
    val uiState = uiStateFlow.value
    val opState = uiState.operationState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                enabled = opState !is OperationState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = opState !is OperationState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = opState !is OperationState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (opState is OperationState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                        if (email.isBlank() || password.isBlank()) {
                            localError = "Por favor completa todos los campos"
                        } else if (!email.matches(emailPattern.toRegex())) {
                            localError = "Formato de correo inválido"
                        } else if (password.length < 6) {
                            localError = "La contraseña debe tener al menos 6 caracteres"
                        } else if (password != confirmPassword) {
                            localError = "Las contraseñas no coinciden"
                        } else {
                            localError = null
                            authViewModel.register(email, password) {
                                onRegisterSuccess()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { onNavigateToLogin() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión aquí")
                }
            }

            val displayedError = localError ?: (if (opState is OperationState.Error) opState.message else null)
            displayedError?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
