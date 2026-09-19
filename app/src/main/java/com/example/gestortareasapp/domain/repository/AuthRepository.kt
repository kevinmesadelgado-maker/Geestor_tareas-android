package com.example.gestortareasapp.domain.repository

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
    fun getCurrentUserId(): String?
}
