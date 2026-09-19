package com.example.gestortareasapp.data.repository

import com.example.gestortareasapp.data.local.dao.TaskDraftDao
import com.example.gestortareasapp.data.local.entity.TaskDraftEntity
import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DraftRepositoryImpl @Inject constructor(
    private val taskDraftDao: TaskDraftDao
) : DraftRepository {

    override fun getDrafts(ownerId: String): Flow<List<Task>> {
        return taskDraftDao.getDrafts(ownerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveDraft(task: Task) {
        taskDraftDao.insertDraft(TaskDraftEntity.fromDomain(task))
    }

    override suspend fun deleteDraft(localId: Int) {
        taskDraftDao.deleteDraftById(localId)
    }
}
