package com.billsAplication.domain.billsUseCases.room

import com.billsAplication.domain.model.NoteItem
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val repo : BillsListRepository) {
    operator fun invoke(item: NoteItem) = repo.deleteNote(item = item)
}