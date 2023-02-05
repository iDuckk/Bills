package com.billsAplication.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private val RESOURCE = "GLOBAL"

    @JvmField
    var countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(){
        countingIdlingResource.increment()
    }

    fun decrement(){
        if(countingIdlingResource.isIdleNow){
            countingIdlingResource.decrement()
        }
    }

}