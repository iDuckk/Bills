package com.billsAplication.utils

import com.billsAplication.presentation.mainActivity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mobile.ads.banner.BannerAdView

interface InterfaceMainActivity {
    fun navBottom(): BottomNavigationView

    suspend fun splash()

    fun yandexAds() : BannerAdView

}