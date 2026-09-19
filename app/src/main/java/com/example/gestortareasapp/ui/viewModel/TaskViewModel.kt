package com.example.gestortareasapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.usecase.draft.DeleteDraftUseCase
import com.example.gestortareasapp.domain.usecase.draft.GetDraftsUseCase
import com.example.gestortareasapp.domain.usecase.draft.SaveDraftUseCase
import com.example.gestortareasapp.domain.usecase.task.CreateTaskUseCase
import com.example.gestortareasapp.domain.usecase.task.DeleteTaskUseCase
import com.example.gestortareasapp.domain.usecase.task.GetTasksUseCase
import com.example.gestortareasapp.domain.usecase.task.UpdateTaskUseCase
import com.example.gestortareasapp.ui.state.OperationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val drafts: List<Task> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getDraftsUseCase: GetDraftsUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val deleteDraftUseCase: DeleteDraftUseCase
) : ViewModel() {

    private val _listUiState = MutableStateFlow(TaskListUiState())
    val listUiState: StateFlow<TaskListUiState> = _listUiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    fun loadTasks(ownerId: String) {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Cargar borradores locales de Room (Flow)
            val draftsJob = launch {
                getDraftsUseCase(ownerId).collect { localDrafts ->
                    _listUiState.update { it.copy(drafts = localDrafts) }
                }
            }

            // Cargar tareas de Firestore
            try {
                val result = getTasksUseCase(ownerId)
                _listUiState.update { it.copy(tasks = result, isLoading = false) }
            } catch (e: Exception) {
                _listUiState.update { it.copy(errorMessage = e.message ?: "Error al cargar tareas", isLoading = false) }
            }
        }
    }

    fun createTask(task: Task, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val taskWithDate = task.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
            val result = createTaskUseCase(taskWithDate)
            
            result.onSuccess {
                _operationState.value = OperationState.Success
                onSuccess()
                loadTasks(task.ownerId)
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "Error al crear tarea")
            }
        }
    }

    fun saveDraft(task: Task, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            saveDraftUseCase(task.copy(createdAt = System.currentTimeMillis()))
            _operationState.value = OperationState.Success
            onSuccess()
        }
    }

    fun publishDraft(draft: Task, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val result = createTaskUseCase(draft.copy(id = "", updatedAt = System.currentTimeMillis()))
            
            result.onSuccess {
                deleteDraftUseCase(draft.id.toInt())
                _operationState.value = OperationState.Success
                onSuccess()
                loadTasks(draft.ownerId)
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "Error al publicar borrador")
            }
        }
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            deleteDraftUseCase(draftId.toInt())
        }
    }

    fun updateTask(task: Task, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val taskWithDate = task.copy(updatedAt = System.currentTimeMillis())
            val result = updateTaskUseCase(taskWithDate)
            
            result.onSuccess {
                _operationState.value = OperationState.Success
                onSuccess()
                loadTasks(task.ownerId)
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "Error al actualizar tarea")
            }
        }
    }

    fun deleteTask(taskId: String, ownerId: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val result = deleteTaskUseCase(taskId)
            
            result.onSuccess {
                _operationState.value = OperationState.Success
                loadTasks(ownerId)
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "Error al eliminar tarea")
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(completed = !task.completed, updatedAt = System.currentTimeMillis())
            updateTaskUseCase(updatedTask).onSuccess {
                loadTasks(task.ownerId)
            }
        }
    }

    fun getTaskById(taskId: String): Task? {
        return _listUiState.value.tasks.find { it.id == taskId }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
