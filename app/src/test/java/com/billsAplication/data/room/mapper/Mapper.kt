package com.billsAplication.data.room.mapper

import android.content.ClipData.Item
import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.domain.model.BillsItem
import org.junit.Assert
import org.junit.Test

class Mapper {

    private val entity = BillEntity(
        id = 1,
        type = 1,
        month = "DECEMBER 2022",
        date = "10/12/2022",
        time = "10:00 AM",
        category = "Food",
        amount = "100.0",
        note = "Lenta",
        description = "",
        bookmark = false,
        image1 = "",
        image2 = "",
        image3 = "",
        image4 = "",
        image5 = "")

    private val item = BillsItem(
        id = 1,
        type = 1,
        month = "DECEMBER 2022",
        date = "10/12/2022",
        time = "10:00 AM",
        category = "Food",
        amount = "100.0",
        note = "Lenta",
        description = "",
        bookmark = false,
        image1 = "",
        image2 = "",
        image3 = "",
        image4 = "",
        image5 = "")

    @Test
    fun `map BillEntity To BillItem`() {

        val expected = BillMapper().mapBillItemToBillEntity(item = item)

        Assert.assertEquals(entity, expected)

    }

    @Test
    fun `map BillItem To BillEntity`() {

        val expected = BillMapper().mapBillEntityToBillItem(item = entity)

        Assert.assertEquals(item, expected)

    }

    @Test
    fun `map BillEntity To BillItemList`() {

        val listItem = listOf<BillsItem>(item, item, item)
        val listEntity = listOf<BillEntity>(entity, entity, entity)

        val expected = BillMapper().mapBillEntityToBillItemList(list = listEntity)

        Assert.assertEquals(listItem, expected)

    }

}