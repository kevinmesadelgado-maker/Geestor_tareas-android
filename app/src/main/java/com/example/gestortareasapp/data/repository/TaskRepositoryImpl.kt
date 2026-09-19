package com.example.gestortareasapp.data.repository

import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRepository {

    private val tasksCollection = firestore.collection("tasks")

    override suspend fun getTasks(ownerId: String): List<Task> {
        return try {
            val snapshot = tasksCollection
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createTask(task: Task): Result<Unit> {
        return try {
            val docRef = tasksCollection.document()
            val newTask = task.copy(id = docRef.id)
            docRef.set(newTask).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            tasksCollection.document(task.id).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            tasksCollection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}