package com.billsAplication.data.room.mapper

import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class BillMapper @Inject constructor(){

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
        bookmark = item.bookmark,
        image1 = item.image1,
        image2 = item.image2,
        image3 = item.image3,
        image4 = item.image4,
        image5 = item.image5
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
        bookmark = item.bookmark,
        image1 = item.image1,
        image2 = item.image2,
        image3 = item.image3,
        image4 = item.image4,
        image5 = item.image5
    )


}