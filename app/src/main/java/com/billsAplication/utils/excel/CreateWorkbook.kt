package com.billsAplication.utils.excel

import android.app.Application
import android.util.Log
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import javax.inject.Inject
import kotlin.collections.ArrayList

class CreateWorkbook @Inject constructor(private val application: Application) {

    operator fun invoke(list: ArrayList<BillsItem>): Workbook {
        // Creating a workbook object from the XSSFWorkbook() class
        val ourWorkbook = XSSFWorkbook() //HSSFWorkbook
        //Creating a sheet called "Bills" inside the workbook and then add data to it
        val sheet: Sheet = ourWorkbook.createSheet(BILLS)
        //Create list Type Expense, Income
        val cList = createList(list)
        val row = sheet.createRow(0)
        //Create title row
        titleRow(row)
        //Parse data as Excel pages
        cList.forEachIndexed { index, billsItem ->
            val row = sheet.createRow(index + 1)
            //Adding data to each  cell
            createCell(row, 0, billsItem.date)
            createCell(row, 1, billsItem.time)
            createCell(row, 2, if(billsItem.type == TYPE_EXPENSES) TYPE_EXPENSES_TXT else TYPE_INCOME_TXT)
            createCell(row, 3, billsItem.amount)
            createCell(row, 4, billsItem.category)
            createCell(row, 5, billsItem.note)
            createCell(row, 6, billsItem.description)
        }
        return ourWorkbook
    }

    private fun titleRow(row: Row) {
        createCell(row, 0, application.getString(R.string.title_date))
        createCell(row, 1, application.getString(R.string.title_time))
        createCell(
            row, 2, application.getString(R.string.bill_list_expense) + "/" +
                    application.getString(R.string.bills_list_income)
        )
        createCell(row, 3, application.getString(R.string.title_amount))
        createCell(row, 4, application.getString(R.string.title_category))
        createCell(row, 5, application.getString(R.string.title_note))
        createCell(row, 6, application.getString(R.string.title_description))
    }

    private fun createList(list: ArrayList<BillsItem>):ArrayList<BillsItem>{
        val correctList: ArrayList<BillsItem> = ArrayList()
        list.forEach {
            if(it.type == TYPE_EXPENSES || it.type == TYPE_INCOME)
                correctList.add(it)
        }
        return correctList
    }

    companion object{
        private val TYPE_EXPENSES = 0
        private val TYPE_INCOME = 1
        private val TYPE_EXPENSES_TXT = "Expenses"
        private val TYPE_INCOME_TXT = "Income"
        private val BILLS = "Bills"
        private val createCell = CreateCell()
    }
}