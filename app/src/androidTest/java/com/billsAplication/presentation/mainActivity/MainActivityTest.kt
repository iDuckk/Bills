package com.billsAplication.presentation.mainActivity

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.room.migration.bundle.VIEW_NAME_PLACEHOLDER
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.billsAplication.R
import com.github.mikephil.charting.charts.PieChart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainActivityTest{

    @get:Rule
    var activityScenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun navigationBottom_GoTo_ShopList(){

        Thread.sleep(1000)

        activityScenario.scenario.onActivity { activity ->

            activity.navBottom().selectedItemId = R.id.shopListFragment

            val recView = activity.findViewById<RecyclerView>(R.id.recViewBill)

            Assert.assertEquals(true, recView.isVisible)

        }
//        onView(withId(R.id.bottom_navigation)).perform(navigateTo(R.id.shopListFragment))
    }

    @Test
    fun navigationBottom_GoTo_Analytics(){

        Thread.sleep(1000)

        activityScenario.scenario.onActivity { activity ->

            activity.navBottom().selectedItemId = R.id.analyticsFragment

        }

        onView(withId(R.id.pieChart)).perform(click())

        onView(withId(R.id.recViewAnalytics)).check(matches(isDisplayed()))

    }

    @Test
    fun navigationBottom_GoTo_Settings(){

        Thread.sleep(1000)

        activityScenario.scenario.onActivity { activity ->

            activity.navBottom().selectedItemId = R.id.settingsFragment

        }

        onView(withId(R.id.b_language)).check(matches(isDisplayed()))

    }

}