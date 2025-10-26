package com.billsAplication.presentation.analytics

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.GetMonthLDUseCase
import com.billsAplication.domain.billsUseCases.room.GetMonthListByTypeCategoryUseCase
import com.billsAplication.domain.billsUseCases.room.GetMonthListByTypeUseCase
import com.billsAplication.domain.billsUseCases.room.SummaryAmountUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.utils.MapMonth
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

class AnalyticsViewModel @Inject constructor(
    private val getMonthListByTypeUseCase : GetMonthListByTypeUseCase,
    private val summaryAmount : SummaryAmountUseCase,
    private val getMonthListByTypeCategoryUseCase : GetMonthListByTypeCategoryUseCase,
    private val application: Application
) : ViewModel() {

    fun summaryAmount(month: String, type: Int) =
        summaryAmount.invoke(month = MapMonth.mapMonthToSQL(month = month, context = application), type = type)

    fun getMonthListByType(month: String, type: Int) =
        getMonthListByTypeUseCase.invoke(month = MapMonth.mapMonthToSQL(month = month, context = application), type = type)

    fun getMonthListByTypeCategory(month: String, type: Int, category: String) =
        getMonthListByTypeCategoryUseCase.invoke(month = MapMonth.mapMonthToSQL(month = month, context = application), type = type, category = category)
}