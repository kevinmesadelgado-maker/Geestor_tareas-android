package com.example.gestortareasapp.domain.usecase.task

import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(ownerId: String): List<Task> {
        return taskRepository.getTasks(ownerId)
    }
}