package com.billsAplication.presentation.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
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

//    val repo = BillsListRepositoryImpl(application)
//    val item : BillsItem
//    item = BillsItem( 0, 0, "June", "23.06.2022", "13:15", "Food",100.0, "Lenta", "0", "0")
//        CoroutineScope(IO).launch {
//            repo.addItem(item)
            //repo.deleteItem(BillsItem( 112, 2, "3", "1", "2", "3",1.0, "2", "3", "1"))
//        }
//        val ITEMS : LiveData<List<BillsItem>> = repo.getAllDataList()
//        ITEMS.observe(this) {
//            Log.d("TAG", " MainActivity: $it")
//        }
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerViewMain, fragment).commit()
    }

    //Version 1.6.1
    //TODO заменить Description на Note в Адаптере and import com.billsAplication.databinding.BillItemBinding
}