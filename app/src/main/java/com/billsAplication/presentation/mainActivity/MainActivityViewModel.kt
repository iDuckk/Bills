package com.billsAplication.presentation.mainActivity

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsAplication.domain.billsUseCases.room.AddCategoryUseCase
import com.billsAplication.domain.billsUseCases.room.GetOldCategoryListUseCase
import com.billsAplication.domain.model.CategoryBillItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

class MainActivityViewModel @Inject constructor(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val getOldCategoryListUseCase: GetOldCategoryListUseCase,
    private val application: Application
): ViewModel() {

    private val MIGRATION_CATEGORIES_DONE = "migration_categories_done"
    private val TYPE_CATEGORY_EXPENSES = 2
    private val TYPE_CATEGORY_INCOME = 4

    fun migrateCategories() {
        val sharedPref = application.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
        val isMigrated = sharedPref.getBoolean(MIGRATION_CATEGORIES_DONE, false)

        if (!isMigrated) {
            viewModelScope.launch(Dispatchers.IO) {
                // Переносим расходы
                getOldCategoryListUseCase.invoke(TYPE_CATEGORY_EXPENSES)
                    .map { it.category.trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .forEach { trimmedCategory ->
                        addCategoryUseCase.invoke(
                            CategoryBillItem(id = 0, typeCategory = TYPE_CATEGORY_EXPENSES, category = trimmedCategory)
                        )
                    }
                // Переносим доходы
                getOldCategoryListUseCase.invoke(TYPE_CATEGORY_INCOME)
                    .map { it.category.trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .forEach { trimmedCategory ->
                        addCategoryUseCase.invoke(
                            CategoryBillItem(id = 0, typeCategory = TYPE_CATEGORY_INCOME, category = trimmedCategory)
                        )
                    }
                // Сохраняем флаг
                sharedPref.edit { putBoolean(MIGRATION_CATEGORIES_DONE, true) }
            }
        }
    }

}
