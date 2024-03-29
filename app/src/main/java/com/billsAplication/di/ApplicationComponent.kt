package com.billsAplication.di

import android.app.Application
import com.billsAplication.presentation.addBill.AddBillFragment
import com.billsAplication.presentation.analytics.AnalyticsFragment
import com.billsAplication.presentation.billsList.BillsListFragment
import com.billsAplication.presentation.bookmarks.BookmarksFragment
import com.billsAplication.presentation.fragmentDialogCategory.FragmentDialogCategory
import com.billsAplication.presentation.search.SearchFragment
import com.billsAplication.presentation.settings.SettingsFragment
import com.billsAplication.presentation.shopList.ShopListFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class])
interface ApplicationComponent {

    fun inject(fragment: BillsListFragment)

    fun inject(fragment: FragmentDialogCategory)

    fun inject(fragment: AddBillFragment)

    fun inject(fragment: BookmarksFragment)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: AnalyticsFragment)

    fun inject(fragment: ShopListFragment)

    fun inject(fragment: SettingsFragment)

    @Component.Factory
    interface Factory{

        fun  create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }

}