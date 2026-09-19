package com.example.gestortareasapp.ui.screen.taskform

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.ui.state.OperationState
import com.example.gestortareasapp.ui.viewModel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    viewModel: TaskViewModel,
    ownerId: String,
    taskId: String? = null,
    onTaskSaved: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var existingTask by remember { mutableStateOf<Task?>(null) }
    
    val opStateFlow = viewModel.operationState.collectAsState()
    val opState = opStateFlow.value

    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = viewModel.getTaskById(taskId)
            if (task != null) {
                existingTask = task
                title = task.title
                description = task.description
            }
        }
    }

    // Resetear el estado al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetOperationState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nueva Tarea" else "Editar Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                enabled = opState !is OperationState.Loading
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = opState !is OperationState.Loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (opState is OperationState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (taskId == null) {
                                val newTask = Task(
                                    ownerId = ownerId,
                                    title = title,
                                    description = description
                                )
                                viewModel.createTask(newTask) {
                                    onTaskSaved()
                                }
                            } else {
                                existingTask?.let {
                                    val updatedTask = it.copy(
                                        title = title,
                                        description = description
                                    )
                                    viewModel.updateTask(updatedTask) {
                                        onTaskSaved()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (taskId == null) "Guardar Tarea" else "Actualizar Tarea")
                }

                if (taskId == null) {
                    OutlinedButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                val draft = Task(
                                    ownerId = ownerId,
                                    title = title,
                                    description = description
                                )
                                viewModel.saveDraft(draft) {
                                    onTaskSaved()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar como Borrador (Local)")
                    }
                }
            }

            if (opState is OperationState.Error) {
                Text(
                    text = opState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
