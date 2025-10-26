package com.billsAplication.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion
import javax.inject.Inject

@SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
class StateColorButton @Inject constructor(
    private val application: Application
) {

    fun colorButtons(type: Int = TYPE_EQUALS): Int {
            return when (type) {
                TYPE_INCOME -> application.getColor(R.color.text_income)
                TYPE_EXPENSES -> application.getColor(R.color.text_expense)
                else -> application.getColor(R.color.text_expense)
            }
    }

    @JvmName("getColorAddButton1")
    fun colorAddButton(type: Int = TYPE_EQUALS): Drawable? {
        return when (type) {
            TYPE_INCOME -> application.getDrawable(R.drawable.double_color_button_income)
            TYPE_EXPENSES -> application.getDrawable(R.drawable.double_color_button_expenses)
            else -> application.getDrawable(R.drawable.double_color_button)
        }
    }

    fun stateNavBot(type: Int = TYPE_EQUALS): ColorStateList {
        return when (type) {
            TYPE_INCOME -> application.getColorStateList(R.drawable.selector_item_bot_nav_income)
            TYPE_EXPENSES -> application.getColorStateList(R.drawable.selector_item_bot_nav)
            else -> application.getColorStateList(R.drawable.selector_item_bot_nav)
        }
    }

    fun gradientButton(type: Int = TYPE_EQUALS): Array<Float> {
        return when (type) {
            TYPE_INCOME -> arrayOf(100f, 180f)
            TYPE_EXPENSES -> arrayOf(20f, 100f)
            else -> arrayOf(0f, 190f)
        }
    }

    companion object {
        private val TYPE_EXPENSES = 0
        private val TYPE_INCOME = 1
        private val TYPE_EQUALS = 2
        var CURRENT_TYPE = TYPE_EXPENSES

        fun getColorType(context: Context, type: Int): Color {
            return when (type) {
                BillsItem.TYPE_EXPENSES -> {
                    Color(context.getColor(R.color.text_expense))
                }
                BillsItem.TYPE_INCOME -> {
                    Color(context.getColor(R.color.text_income))
                }
                else -> {
                    Color(context.getColor(R.color.text_expense))
                }
            }
        }

        fun getColorCurrentType(context: Context): Color {
            return when (CURRENT_TYPE) {
                BillsItem.TYPE_EXPENSES -> {
                    Color(context.getColor(R.color.text_expense))
                }
                BillsItem.TYPE_INCOME -> {
                    Color(context.getColor(R.color.text_income))
                }
                else -> {
                    Color(context.getColor(R.color.text_expense))
                }
            }
        }

    }

}
