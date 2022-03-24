package com.billsAplication.presentation.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        initBottomNavigation()

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

    fun initBottomNavigation(){
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    //Rebuild initBottomNavigation()
    //Fragment AddBill
}