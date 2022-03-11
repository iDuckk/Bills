package com.billsAplication.data.room.mapper

import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.domain.model.BillsItem

class BillMapper {

    fun mapBillItemToBillEntity(item : BillsItem) = BillEntity(
        id = item.id,
        type = item.type,
        month = item.month,
        date = item.date,
        time = item.time,
        category = item.category,
        amount = item.amount,
        note = item.note,
        description = item.description,
        urlImage = item.urlImage
    )

    fun mapBillEntityToBillItem(item : BillEntity) = BillsItem(
        id = item.id,
        type = item.type,
        month = item.month,
        date = item.date,
        time = item.time,
        category = item.category,
        amount = item.amount,
        note = item.note,
        description = item.description,
        urlImage = item.urlImage
    )


}