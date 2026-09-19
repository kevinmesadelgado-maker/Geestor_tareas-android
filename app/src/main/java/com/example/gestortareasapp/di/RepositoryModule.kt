package com.example.gestortareasapp.di

import com.example.gestortareasapp.data.repository.AuthRepositoryImpl
import com.example.gestortareasapp.data.repository.DraftRepositoryImpl
import com.example.gestortareasapp.data.repository.TaskRepositoryImpl
import com.example.gestortareasapp.domain.repository.AuthRepository
import com.example.gestortareasapp.domain.repository.DraftRepository
import com.example.gestortareasapp.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindDraftRepository(
        draftRepositoryImpl: DraftRepositoryImpl
    ): DraftRepository
}
