package com.billsAplication.utils

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.billsAplication.data.room.billsDb.BillDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class ImportDatabaseFile@Inject constructor(val application: Application) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(inStream: InputStream) {
        try {

            val outStream = FileOutputStream(application.getDatabasePath(nameDatabase).path)
            inStream.use { input ->
                outStream.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{

        private const val nameDatabase = "bills_database"

    }
}