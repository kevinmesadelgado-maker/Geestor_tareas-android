package com.example.gestortareasapp.domain.usecase.task

import com.example.gestortareasapp.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.deleteTask(taskId)
    }
}
