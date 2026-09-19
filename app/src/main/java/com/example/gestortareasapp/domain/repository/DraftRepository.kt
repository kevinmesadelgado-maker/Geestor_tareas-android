package com.example.gestortareasapp.domain.repository

import com.example.gestortareasapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    fun getDrafts(ownerId: String): Flow<List<Task>>
    suspend fun saveDraft(task: Task)
    suspend fun deleteDraft(localId: Int)
}
