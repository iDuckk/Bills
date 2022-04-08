package com.billsAplication.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class DeleteFIle @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(strPath: String) {

        val path = Paths.get(strPath)

        try {
            val result = Files.deleteIfExists(path)
            if (result) println("Deletion succeeded.") else println("Deletion failed.")
        } catch (e: IOException) {
            println("Deletion failed.")
            e.printStackTrace()
        }
    }

}