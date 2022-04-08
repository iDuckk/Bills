package com.billsAplication.utils

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateImageFile @Inject constructor(val application: Application) {

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    operator fun invoke(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}