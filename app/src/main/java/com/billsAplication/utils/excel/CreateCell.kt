package com.billsAplication.utils.excel

import org.apache.poi.ss.usermodel.Row
import javax.inject.Inject

class CreateCell @Inject constructor() {

    operator fun invoke(sheetRow: Row, columnIndex: Int, cellValue: String?) {
        //create a cell at a passed in index
        val ourCell = sheetRow.createCell(columnIndex)
        //add the value to it
        //a cell can be empty. That's why its nullable
        ourCell?.setCellValue(cellValue)
    }
}