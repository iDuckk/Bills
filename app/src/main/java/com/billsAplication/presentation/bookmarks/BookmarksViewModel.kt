package com.billsAplication.presentation.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.domain.billsUseCases.GetBookmarksUseCase
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

    private val TYPE_BOOKMARK = true

    lateinit var list : LiveData<List<BillsItem>>

    init {
        getBookmarksList()
    }

    fun getBookmarksList() {
        list =  getBookmarksUseCase.invoke(TYPE_BOOKMARK)
    }

}