package com.billsAplication.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import com.billsAplication.di.ApplicationScope
import com.billsAplication.domain.repository.BillsListRepository
import javax.inject.Inject

@ApplicationScope
class StateColorButton @Inject constructor(){
    var colorAddButton: Drawable? = null
        set(value) {
            field = value
        }
        get() = field

    var colorButtons: Int? = null
        set(value) {
            field = value
        }
        get() = field

    var stateNavBot: ColorStateList? = null
    set(value){
        field = value
    }
    get() = field
}
