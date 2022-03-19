package com.billsAplication

import android.app.Application
import com.billsAplication.di.DaggerApplicationComponent

class BillsApplication: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }

}