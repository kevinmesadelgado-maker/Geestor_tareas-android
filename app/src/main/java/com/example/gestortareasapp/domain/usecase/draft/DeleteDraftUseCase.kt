package com.example.gestortareasapp.domain.usecase.draft

import com.example.gestortareasapp.domain.repository.DraftRepository
import javax.inject.Inject

class DeleteDraftUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    suspend operator fun invoke(localId: Int) {
        draftRepository.deleteDraft(localId)
    }
}
