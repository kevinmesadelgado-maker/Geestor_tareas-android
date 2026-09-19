package com.example.gestortareasapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestortareasapp.domain.model.Task

@Entity(tableName = "task_drafts")
data class TaskDraftEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val ownerId: String,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Task = Task(
        id = localId.toString(), // Usamos el ID local como temporal
        ownerId = ownerId,
        title = title,
        description = description,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(task: Task): TaskDraftEntity = TaskDraftEntity(
            ownerId = task.ownerId,
            title = task.title,
            description = task.description,
            createdAt = if (task.createdAt == 0L) System.currentTimeMillis() else task.createdAt
        )
    }
}
