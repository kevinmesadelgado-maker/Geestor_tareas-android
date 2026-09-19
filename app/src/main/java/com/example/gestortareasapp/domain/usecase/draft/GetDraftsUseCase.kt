package com.example.gestortareasapp.domain.usecase.draft

import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDraftsUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    operator fun invoke(ownerId: String): Flow<List<Task>> {
        return draftRepository.getDrafts(ownerId)
    }
}
