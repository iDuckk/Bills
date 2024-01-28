package com.billsAplication.data.room.mapper

import com.billsAplication.data.room.model.BillEntity
import com.billsAplication.data.room.model.NoteEntity
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.NoteItem
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

    fun mapBillEntityToBillItemList(list: List<BillEntity>): ArrayList<BillsItem>{
        val newList = ArrayList<BillsItem>()
        list.forEach { item ->
            newList.add(
                BillsItem(
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
            ))
        }
        return newList
    }

    fun mapNoteEntityToNoteItemList(list: List<NoteEntity>): ArrayList<NoteItem>{
        val newList = ArrayList<NoteItem>()
        list.forEach { item ->
            newList.add(
                NoteItem(
                    id = item.id,
                    dateTimeCreation = item.dateTimeCreation,
                    textNote = item.textNote,
                    color = item.color
                    ))
        }
        return newList
    }

    fun mapNoteItemToNoteEntity(item : NoteItem) = NoteEntity(
        id = item.id,
        dateTimeCreation = item.dateTimeCreation,
        textNote = item.textNote,
        color = item.color
    )

}