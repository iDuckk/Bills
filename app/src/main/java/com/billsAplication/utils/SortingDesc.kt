package com.billsAplication.utils

import com.billsAplication.domain.model.BillsItem
import javax.inject.Inject

class SortingDesc@Inject constructor() {
    operator fun invoke(list: MutableList<BillsItem>): List<BillsItem>{
        //Sorting of date and time
        for (i in list.indices){
            var j = i -1 //first element of While
            while (j > 0){
                if(date.invoke(list[j].date + list[j].time)
                    > date.invoke(list[j + 1].date + list[j + 1].time))
                    list[j] = list[j + 1].also { list[j + 1] = list[j] }
                j--
            }
        }
        return list.reversed()
    }

    companion object{
        private val date = CreateDate()
    }
}