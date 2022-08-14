package com.billsAplication.utils

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SimpleSQLiteQuery
import com.billsAplication.data.room.billsDb.BillDatabase
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
class ExportDatabaseFile@Inject constructor(val application: Application) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke() {
        try {
            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase).path, databaseBackupDir + nameDatabase + "_$timeStamp")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{
        private val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        private const val nameDatabase = "bills_database"
        private var databaseBackupDir = Environment.getExternalStorageDirectory().path + "/Download/"

        private fun copyDataFromOneToAnother(fromPath: String, toPath: String) {
            val inStream = File(fromPath).inputStream()
            val outStream = FileOutputStream(toPath)

            inStream.use { input ->
                outStream.use { output ->
                    input.copyTo(output)
                }
            }
            inStream.close()
            outStream.flush()
            outStream.close()
        }
    }
}