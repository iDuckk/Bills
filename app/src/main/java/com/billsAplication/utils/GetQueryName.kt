package com.billsAplication.utils

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import javax.inject.Inject

class GetQueryName @Inject constructor(val application: Application) {
    operator fun invoke(uri: Uri): String? {
        val returnCursor: Cursor = application.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}