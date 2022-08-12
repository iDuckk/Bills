package com.billsAplication.utils

import android.app.Application
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
import javax.inject.Inject

class ExportDatabaseFile@Inject constructor(val application: Application) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke() {
        try {
//            File(databaseBackupDir).apply {
//                mkdirs()
//            }
            var databaseBackupDir1 = application.getExternalFilesDir("Download")
            val path = databaseBackupDir1!!.toPath().toString() +"/" + nameDatabase
//            BillDatabase.getDatabase(application).billDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase).path, databaseBackupDir + nameDatabase)
//            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase + "-shm").path, databaseBackupDir + "backup_" + nameDatabase + "-shm")
//            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase + "-wal").path, databaseBackupDir + "backup_" + nameDatabase + "-wal")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{

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
        }
    }
}