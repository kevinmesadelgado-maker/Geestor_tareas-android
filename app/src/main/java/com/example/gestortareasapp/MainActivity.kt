package com.example.gestortareasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gestortareasapp.ui.navigation.AppNavigation
import com.example.gestortareasapp.ui.viewModel.AuthViewModel
import com.example.gestortareasapp.ui.viewModel.TaskViewModel
import com.example.gestortareasapp.ui.theme.GestorTareasAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // para que Hilt inyecte los ViewModels aquí
class MainActivity : ComponentActivity() {

    // Inyectamos ambos ViewModels usando las extensiones de Hilt
    private val authViewModel: AuthViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GestorTareasAppTheme {
                // Llamamos a nuestro mapa de rutas principal que conecta Login, Lista y Formulario
                AppNavigation(
                    authViewModel = authViewModel,
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}
