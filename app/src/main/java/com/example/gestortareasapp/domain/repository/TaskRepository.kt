package com.example.gestortareasapp.domain.repository

import com.example.gestortareasapp.domain.model.Task

interface TaskRepository {
    suspend fun getTasks(ownerId: String): List<Task>
    suspend fun createTask(task: Task): Result<Unit>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
}

