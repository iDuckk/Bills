package com.billsAplication.utils

import com.billsAplication.domain.model.BillsItem

sealed class StateBillsList

class Error(val exception: String): StateBillsList()
object Progress : StateBillsList()
class Result(val list: ArrayList<BillsItem>) : StateBillsList()
