package com.billsAplication.presentation.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.billsAplication.R
import com.billsAplication.data.room.repository.BillsListRepositoryImpl
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//    val item : BillsItem
//    item = BillsItem( 3, 2, "3", "1", "2", "3",1.0, "2", "3", "1")
//    val repo = BillsListRepositoryImpl(application)
//        CoroutineScope(IO).launch {
//            repo.addItem(item)
//        }
    }
}
