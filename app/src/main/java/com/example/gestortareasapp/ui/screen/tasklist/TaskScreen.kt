package com.example.gestortareasapp.ui.screen.tasklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun TaskScreen(
    viewModel: TaskViewModel,
    ownerId: String,
    onAddTask: () -> Unit,
    onEditTask: (String) -> Unit,
    onLogout: () -> Unit
) {
    val listUiState by viewModel.listUiState.collectAsState()
    val opStateFlow = viewModel.operationState.collectAsState()
    val opState = opStateFlow.value
    
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (listUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (listUiState.drafts.isNotEmpty()) {
                        item {
                            Text("Borradores Locales", style = MaterialTheme.typography.titleSmall)
                        }
                        items(listUiState.drafts) { draft ->
                            TaskItem(
                                task = draft,
                                isDraft = true,
                                onToggleCompletion = { },
                                onEdit = { },
                                onDelete = { viewModel.deleteDraft(draft.id) },
                                onPublish = { viewModel.publishDraft(draft) {} }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    if (listUiState.tasks.isEmpty() && listUiState.drafts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes tareas pendientes")
                            }
                        }
                    } else {
                        item {
                            Text("Tareas en la Nube", style = MaterialTheme.typography.titleSmall)
                        }
                        items(listUiState.tasks) { task ->
                            TaskItem(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onEdit = { onEditTask(task.id) },
                                onDelete = { showDeleteDialog = task.id }
                            )
                        }
                    }
                }
            }

            if (opState is OperationState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            val errorToShow = listUiState.errorMessage ?: (if (opState is OperationState.Error) opState.message else null)
            errorToShow?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }

    showDeleteDialog?.let { taskId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Tarea") },
            text = { Text("¿Estás seguro de que deseas eliminar esta tarea permanentemente?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(taskId, ownerId)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    isDraft: Boolean = false,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPublish: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isDraft) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isDraft) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onToggleCompletion() }
                )
            } else {
                Icon(Icons.Default.EditNote, contentDescription = "Borrador", modifier = Modifier.padding(12.dp))
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (isDraft) {
                IconButton(onClick = { onPublish?.invoke() }) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Publicar", tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
