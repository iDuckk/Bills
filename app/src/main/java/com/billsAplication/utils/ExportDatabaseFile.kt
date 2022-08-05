package com.billsAplication.utils

import android.app.Application
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class ExportDatabaseFile@Inject constructor(val application: Application) {
    operator fun invoke() {
        try {
            File(databaseBackupDir).apply {
                mkdirs()
            }
            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase).path, databaseBackupDir + "backup_" + nameDatabase)
            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase + "-shm").path, databaseBackupDir + "backup_" + nameDatabase + "-shm")
            copyDataFromOneToAnother(application.getDatabasePath(nameDatabase + "-wal").path, databaseBackupDir + "backup_" + nameDatabase + "-wal")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{

        private const val nameDatabase = "bills_database"
        private var databaseBackupDir = Environment.getExternalStorageDirectory().path + "/Download/bills_backup/"

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