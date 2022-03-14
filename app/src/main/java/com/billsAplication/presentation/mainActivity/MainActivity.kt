package com.billsAplication.presentation.mainActivity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.billsAplication.R
import com.billsAplication.presentation.analytics.AnalyticsFragment
import com.billsAplication.presentation.billsList.BillsListFragment
import com.billsAplication.presentation.settings.SettingsFragment
import com.billsAplication.presentation.shopList.ShopListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val analyticsFragment = AnalyticsFragment()
        val billsListFragment = BillsListFragment()
        val settingsFragment = SettingsFragment()
        val shopListFragment = ShopListFragment()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_list -> replaceFragment(billsListFragment)
                R.id.menu_analytics -> replaceFragment(analyticsFragment)
                R.id.menu_shopList -> replaceFragment(shopListFragment)
                R.id.menu_settings -> replaceFragment(settingsFragment)
            }
            true
        }


//    val item : BillsItem
//    item = BillsItem( 0, 2, "3", "1", "2", "3",1.0, "2", "3", "1")
//    val repo = BillsListRepositoryImpl(application)
//        CoroutineScope(IO).launch {
//            repo.addItem(item)
//            //repo.deleteItem(BillsItem( 101, 2, "3", "1", "2", "3",1.0, "2", "3", "1"))
//        }
    }
    fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerViewMain, fragment).commit()
    }
}

// Remove actionbar
// Create bottomNavigation
// Create Fragments