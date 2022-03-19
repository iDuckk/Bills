package com.billsAplication.di

import android.app.Application
import com.billsAplication.data.room.billsDb.BillDao
import com.billsAplication.data.room.billsDb.BillDatabase
import com.billsAplication.data.room.repository.BillsListRepositoryImpl
import com.billsAplication.domain.repository.BillsListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindBillsListRepository(impl : BillsListRepositoryImpl): BillsListRepository

    companion object{
        @ApplicationScope
        @Provides
        fun providesListDao(application: Application): BillDao {
            return BillDatabase.getDatabase(application).billDao()
        }

    }

}