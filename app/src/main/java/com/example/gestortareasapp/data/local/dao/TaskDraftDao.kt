package com.example.gestortareasapp.data.local.dao

import androidx.room.*
import com.example.gestortareasapp.data.local.entity.TaskDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDraftDao {
    @Query("SELECT * FROM task_drafts WHERE ownerId = :ownerId")
    fun getDrafts(ownerId: String): Flow<List<TaskDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: TaskDraftEntity)

    @Delete
    suspend fun deleteDraft(draft: TaskDraftEntity)

    @Query("DELETE FROM task_drafts WHERE localId = :localId")
    suspend fun deleteDraftById(localId: Int)
}
