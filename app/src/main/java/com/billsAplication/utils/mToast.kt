package com.billsAplication.utils

import android.app.Application
import android.widget.Toast
import javax.inject.Inject

class mToast @Inject constructor(val application: Application) {

    operator fun invoke(message: String?) {
        mToast?.cancel()
        mToast = Toast.makeText(application, message, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    companion object{
        private var mToast : Toast? = null
    }
}