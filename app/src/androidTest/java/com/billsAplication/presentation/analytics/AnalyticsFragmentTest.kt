package com.billsAplication.presentation.analytics

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.billsAplication.FakeBillsListRepository
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.GetMonthLDUseCase
import com.billsAplication.domain.model.BillsItem
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.ArrayList
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class AnalyticsFragmentTest{

    lateinit var scenario: FragmentScenario<AnalyticsFragment>
    lateinit var navController: TestNavHostController
    lateinit var viewModel: AnalyticsViewModel
    lateinit var application: Application
    lateinit var getMonth : GetMonthLDUseCase
    lateinit var repository: FakeBillsListRepository

    val COUNT_ITEM = 10
    val SELECTED_ITEM_ID = 1
    val list = ArrayList<BillsItem>()
    val item = BillsItem(
        id = 0,
        type = 0,
        month = "DECEMBER 2022",
        date = "10/12/2022",
        time = "10:00 AM",
        category = "Food",
        amount = "100.0",
        note = "note 0",
        description = "",
        bookmark = false,
        image1 = "",
        image2 = "",
        image3 = "",
        image4 = "",
        image5 = ""
    )

    @Before
    fun setUp() {
        // Create a FragmentScenario
        scenario = FragmentScenario.launchInContainer(
            fragmentClass = AnalyticsFragment::class.java, fragmentArgs = Bundle(),
            themeResId = R.style.Theme_Bills, initialState = Lifecycle.State.STARTED)
        // Create a NavController
        navController = TestNavHostController(context = ApplicationProvider.getApplicationContext())
        //Init Navigation
        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Set navigate controller
            Navigation.setViewNavController( view = fragment.requireView(), controller = navController)
            // Then navigate
            navController.navigate(resId = R.id.analyticsFragment)
            //Application
            application = fragment.requireActivity().application
        }
        //InitViewModel
        repository = FakeBillsListRepository()
        getMonth = GetMonthLDUseCase(repo = repository)
        viewModel = AnalyticsViewModel(getMonth = getMonth, application = application)
    }

    @Test
    fun testing_pieChart(){

        onView(withId(R.id.pieChart)).check(matches(isDisplayed()))

        onView(withId(R.id.pieChart)).perform(click())

    }

    @Test
    fun testing_bar_previousMonth(){

        onView(withId(R.id.im_backMonth_analytics)).check(matches(isDisplayed()))

        onView(withId(R.id.im_backMonth_analytics)).perform(click())

        val actual = viewModel.changeMonthBar(false)

        onView(withId(R.id.tv_month_analytics)).check(matches(withText(actual)))
    }

    @Test
    fun testing_bar_nextMonth(){

        onView(withId(R.id.im_nextMonth_analytics)).check(matches(isDisplayed()))

        onView(withId(R.id.im_nextMonth_analytics)).perform(click())

        val actual = viewModel.changeMonthBar(true)

        onView(withId(R.id.tv_month_analytics)).check(matches(withText(actual)))
    }

    @Test
    fun testing_bar_currentMonth(){

        onView(withId(R.id.im_nextMonth_analytics)).check(matches(isDisplayed()))

        onView(withId(R.id.im_nextMonth_analytics)).perform(click())

        onView(withId(R.id.tv_month_analytics)).perform(click())

        val actual = viewModel.currentDate()

        onView(withId(R.id.tv_month_analytics)).check(matches(withText(actual)))
    }

    @Test
    fun testing_bar_TextViewExpense() {

        onView(withId(R.id.tv_expense))
            .perform(click())

        scenario.onFragment{ fragment ->

            val textView = fragment.requireView().findViewById<TextView>(R.id.tv_expense)
            val textColor = textView.currentTextColor

            Assert.assertEquals(fragment.requireContext()
                .getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating),
                textColor
            )

        }

    }

    @Test
    fun testing_bar_TextViewIncome() {

        onView(withId(R.id.tv_income))
            .perform(click())

        scenario.onFragment{ fragment ->

            val textView = fragment.requireView().findViewById<TextView>(R.id.tv_income)
            val textColor = textView.currentTextColor

            Assert.assertEquals(fragment.requireContext()
                .getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating),
                textColor
            )

        }

    }

    @Test
    fun testing_recView_item(){
        //Create list
        for (i in 0..COUNT_ITEM) {
            list.add(item.copy(id = i, note = "note $i"))
        }

        scenario.onFragment { fragment ->
            fragment.billAdapter.submitList(list)
        }

        onView(withId(R.id.recViewAnalytics)).check(matches(isDisplayed()))

        //Check Contents of Item
        onView(withId(R.id.recViewAnalytics))
            .check(
                matches(
                    CoreMatchers.allOf(
                        childOfViewAtPositionWithMatcher(
                            R.id.tv_Item_Note,
                            SELECTED_ITEM_ID,
                            withText(list[SELECTED_ITEM_ID].note)
                        )
                    )
                )
            )
    }

    @Test
    fun testing_recView_scroll(){
        //Create list
        for (i in 0..COUNT_ITEM) {
            list.add(item.copy(id = i, note = "note $i", type = Random.nextInt(0, 1)))
        }

        scenario.onFragment { fragment ->
            fragment.billAdapter.submitList(list)
        }

        onView(withId(R.id.recViewAnalytics)).check(matches(isDisplayed()))

        onView(withId(R.id.recViewAnalytics))
            .perform(scrollTo())
            .check(
                matches(
                    CoreMatchers.allOf(
                        childOfViewAtPositionWithMatcher(
                            R.id.tv_Item_Note,
                            8,
                            withText(list[8].note)
                        )
                    )
                )
            )
    }

    fun childOfViewAtPositionWithMatcher(childId: Int, position: Int, childMatcher: Matcher<View>) : Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText(
                    "Checks that the matcher childMatcher matches" +
                            " with a view having a given id inside a RecyclerView's item (given its position)"
                )
            }

            override fun matchesSafely(recyclerView: RecyclerView?): Boolean {
                val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)
                val matcher = hasDescendant(CoreMatchers.allOf(withId(childId), childMatcher))
                return viewHolder != null && matcher.matches(viewHolder.itemView)
            }

        }
    }

    fun actionOnChild(action: ViewAction, childId: Int) : ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Perform action on the view whose id is passed in"
            }

            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(isDisplayed(), isAssignableFrom(View::class.java))
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.let {
                    val child: View = it.findViewById(childId)
                    action.perform(uiController, child)
                }
            }

        }
    }

    @ColorInt
    fun Context.getColorFromAttr(@AttrRes attrColor: Int
    ): Int {
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
        val textColor = typedArray.getColor(0, 0)
        typedArray.recycle()
        return textColor
    }

}