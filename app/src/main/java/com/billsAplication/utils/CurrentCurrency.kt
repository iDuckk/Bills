package com.billsAplication.utils

object CurrentCurrency{

    var currency: String = ""
        set(value) {
            field = value
        }
    get() = field

    var type: Boolean = false
        set(value) {
            field = value
        }
        get() = field
}
