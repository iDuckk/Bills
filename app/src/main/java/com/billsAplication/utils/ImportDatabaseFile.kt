package com.billsAplication.utils

import android.app.Application
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class ImportDatabaseFile@Inject constructor(val application: Application) {
    operator fun invoke() {
        try {
            copyDataFromOneToAnother(databaseBackupDir + "backup_" + nameDatabase, application.getDatabasePath(nameDatabase).path)
            copyDataFromOneToAnother(databaseBackupDir + "backup_" + nameDatabase + "-shm", application.getDatabasePath(nameDatabase + "-shm").path)
            copyDataFromOneToAnother(databaseBackupDir + "backup_" + nameDatabase + "-wal", application.getDatabasePath(nameDatabase + "-wal").path)
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