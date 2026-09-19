package com.example.gestortareasapp.domain.usecase.draft

import com.example.gestortareasapp.domain.model.Task
import com.example.gestortareasapp.domain.repository.DraftRepository
import javax.inject.Inject

class SaveDraftUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    suspend operator fun invoke(task: Task) {
        draftRepository.saveDraft(task)
    }
}
