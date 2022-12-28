package com.billsAplication.utils

import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

interface InterfaceMainActivity {
    fun navBottom(): BottomNavigationView

    suspend fun splash()

}