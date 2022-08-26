package com.billsAplication.utils.excel

import android.app.Application
import android.os.Environment
import com.billsAplication.domain.model.BillsItem
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class CreateExcelFile @Inject constructor(private val application: Application) {

    operator fun invoke(list: ArrayList<BillsItem>) {

        //get our app file directory
        val ourAppFileDirectory = Environment.getExternalStorageDirectory().path + "/Download/"
        //Create an excel file called test.xlsx
        val excelFile = File(ourAppFileDirectory, "test.xlsx")
        //Write a workbook to the file using a file outputstream
        try {
            val fileOut = FileOutputStream(excelFile)
            CreateWorkbook(application).invoke(list).write(fileOut)
            fileOut.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}