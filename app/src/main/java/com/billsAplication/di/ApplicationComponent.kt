package com.billsAplication.di

import android.app.Application
import com.billsAplication.presentation.billsList.BillsListFragment
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategory
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class])
interface ApplicationComponent {

    fun inject(fragment: BillsListFragment)

    fun inject(fragment: FragmentDialogCategory)

    @Component.Factory
    interface Factory{

        fun  create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }

}