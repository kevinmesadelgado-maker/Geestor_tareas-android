package com.example.gestortareasapp.domain.model

data class Task(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)