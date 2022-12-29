package com.billsAplication.utils

import com.billsAplication.domain.model.BillsItem

sealed class StateBillsList

class Error(val exception: String): StateBillsList()
class TotalAmountBar(val exp: String = "0.0", val inc: String = "0.0", val tot: String = "0.0") : StateBillsList()
class Result(val list: ArrayList<BillsItem>) : StateBillsList()
class ColorState(val type: Int) : StateBillsList()
