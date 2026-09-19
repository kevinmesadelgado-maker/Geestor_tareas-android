package com.example.gestortareasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestortareasapp.ui.screen.login.LoginScreen
import com.example.gestortareasapp.ui.screen.register.RegisterScreen
import com.example.gestortareasapp.ui.screen.taskform.TaskFormScreen
import com.example.gestortareasapp.ui.screen.tasklist.TaskScreen
import com.example.gestortareasapp.ui.viewModel.AuthViewModel
import com.example.gestortareasapp.ui.viewModel.TaskViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object TaskList : Screen("task_list_screen")
    object TaskForm : Screen("task_form_screen?taskId={taskId}") {
        fun createRoute(taskId: String? = null) = if (taskId != null) "task_form_screen?taskId=$taskId" else "task_form_screen"
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel
) {
    val navController = rememberNavController()
    val isLogged = remember { authViewModel.isUserLoggedIn() }
    
    // Obtenemos el UID real de Firebase Auth si ya hay sesión
    val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Cargar tareas si ya está logueado
    LaunchedEffect(isLogged) {
        if (isLogged && currentUid.isNotBlank()) {
            taskViewModel.loadTasks(currentUid)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLogged) Screen.TaskList.route else Screen.Login.route
    ) {
        // 1. Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    taskViewModel.loadTasks(uid)
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.clearError()
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 2. Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    taskViewModel.loadTasks(uid)
                    navController.navigate(Screen.TaskList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla de la Lista de Tareas
        composable(Screen.TaskList.route) {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            TaskScreen(
                viewModel = taskViewModel,
                ownerId = uid,
                onAddTask = {
                    navController.navigate(Screen.TaskForm.createRoute())
                },
                onEditTask = { tid: String ->
                    navController.navigate(Screen.TaskForm.createRoute(tid))
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.TaskList.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 4. Pantalla del Formulario de Tareas
        composable(Screen.TaskForm.route) { backStackEntry ->
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val tid = backStackEntry.arguments?.getString("taskId")
            TaskFormScreen(
                viewModel = taskViewModel,
                ownerId = uid,
                taskId = tid,
                onTaskSaved = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
