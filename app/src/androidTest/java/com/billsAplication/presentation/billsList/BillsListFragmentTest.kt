package com.billsAplication.presentation.billsList

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.billsAplication.FakeBillsListRepository
import com.billsAplication.R
import com.billsAplication.domain.billsUseCases.DeleteBillItemUseCase
import com.billsAplication.domain.billsUseCases.GetMonthListUseCase
import com.billsAplication.domain.billsUseCases.GetTypeListUseCase
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.shopList.ShopListViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class BillsListFragmentTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var scenario: FragmentScenario<BillsListFragment>
    lateinit var navController: TestNavHostController
    lateinit var application: Application
    lateinit var getMonth: GetMonthListUseCase
    lateinit var delete: DeleteBillItemUseCase
    lateinit var getTypeListUseCase: GetTypeListUseCase
    lateinit var repository: FakeBillsListRepository
    lateinit var viewModel: BillsListViewModel


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
        scenario = FragmentScenario.launchInContainer(
            fragmentClass = BillsListFragment::class.java, fragmentArgs = Bundle(),
            themeResId = R.style.Theme_Bills, initialState = Lifecycle.State.STARTED)

        navController = TestNavHostController(context = ApplicationProvider.getApplicationContext())

        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Set navigate controller
            Navigation.setViewNavController( view = fragment.requireView(), controller = navController)
            // Then navigate
            navController.navigate(resId = R.id.billsListFragment)
            //Application
            application = fragment.requireActivity().application
        }

        repository = FakeBillsListRepository()
        getMonth =  GetMonthListUseCase(repo = repository)
        delete =  DeleteBillItemUseCase(repo = repository)
        getTypeListUseCase =  GetTypeListUseCase(repo = repository)

        viewModel = BillsListViewModel(
            getMonth = getMonth, delete = delete,
            getTypeListUseCase = getTypeListUseCase, application = application
        )

        for (i in 0..COUNT_ITEM) {
            list.add(item.copy(id = i, note = "note $i", type = Random.nextInt(0, 1)))
        }
    }

    @Test
    fun testing_bar_previousMonth() = runTest{

        onView(withId(R.id.im_backMonth))
            .check(matches(isDisplayed()))

        onView(withId(R.id.im_backMonth))
            .perform(click())

        val actual = viewModel.changeMonthBar(false)

        onView(withId(R.id.tv_month))
            .check(matches(withText(actual)))
    }

    @Test
    fun testing_bar_nextMonth(){

        onView(withId(R.id.im_nextMonth)).check(
            matches(
                isDisplayed()
            )
        )

        onView(withId(R.id.im_nextMonth)).perform(click())

        val actual = viewModel.changeMonthBar(true)

        onView(withId(R.id.tv_month)).check(
            matches(
                withText(
                    actual
                )
            )
        )
    }

    @Test
    fun testing_bar_currentMonth(){

        onView(withId(R.id.im_nextMonth)).check(
            matches(isDisplayed())
        )

        onView(withId(R.id.im_nextMonth)).perform(click())

        onView(withId(R.id.tv_month)).perform(click())

        val actual = viewModel.currentDate()

        onView(withId(R.id.tv_month)).check(
            matches(
                withText(
                    actual
                )
            )
        )
    }

    @Test
    fun testing_image_bookmarks() {

        onView(withId(R.id.im_bookmarks)).check(matches(isDisplayed())
        )

        onView(withId(R.id.im_bookmarks)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.bookmarksFragment)

    }

    @Test
    fun testing_image_search() {

        onView(withId(R.id.im_bills_search)).check(
            matches(isDisplayed())
        )

        onView(withId(R.id.im_bills_search)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.searchFragment)

    }

    @Test
    fun testing_filter_visibility() {

        onView(withId(R.id.im_bills_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        onView(withId(R.id.cardView_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        onView(withId(R.id.cardView_filter)).check(matches(not(isDisplayed())))


    }

    @Test
    fun testing_filter_checkIncome() {

        onView(withId(R.id.im_bills_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        onView(withId(R.id.checkBox_income)).check(matches(not(isChecked())))

        onView(withId(R.id.checkBox_income)).perform(click())

        onView(withId(R.id.checkBox_income)).check(matches(isChecked()))

        onView(withId(R.id.checkBox_income)).perform(click())

        onView(withId(R.id.checkBox_income)).check(matches(not(isChecked())))

    }

    @Test
    fun testing_filter_checkExpense() {

        onView(withId(R.id.im_bills_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        onView(withId(R.id.checkBox_expense)).check(matches(not(isChecked())))

        onView(withId(R.id.checkBox_expense)).perform(click())

        onView(withId(R.id.checkBox_expense)).check(matches(isChecked()))

        onView(withId(R.id.checkBox_expense)).perform(click())

        onView(withId(R.id.checkBox_expense)).check(matches(not(isChecked())))

    }

    @Test
    fun testing_filter_checkSorting() {

        onView(withId(R.id.im_bills_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        onView(withId(R.id.checkBox_decDate)).check(matches(not(isChecked())))

        onView(withId(R.id.checkBox_decDate)).perform(click())

        onView(withId(R.id.checkBox_decDate)).check(matches(isChecked()))

        onView(withId(R.id.checkBox_decDate)).perform(click())

        onView(withId(R.id.checkBox_decDate)).check(matches(not(isChecked())))

    }

    @Test
    fun testing_filter_Spinner() {

        onView(withId(R.id.im_bills_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.im_bills_filter)).perform(click())

        scenario.withFragment {
            spinnerAdapter.add("Item 1")
            spinnerAdapter.add("Item 2")
            spinnerAdapter.add("Item 3")

        }

        onView(withId(R.id.spinner_filter)).check(matches(not(isChecked())))

        onView(withId(R.id.spinner_filter)).perform(click())

    }

    @Test
    fun testing_recView_content() {

        val LIST_ITEM_ID = 1

        onView(withId(R.id.recViewBill)).check(matches(isDisplayed()))

        scenario.onFragment { fragment ->

            fragment.billAdapter.submitList(null)

            fragment.billAdapter.submitList(list)

            Thread.sleep(1000)
        }

        //Check Contents of Item
        onView(withId(R.id.recViewBill))
            .check(
                matches(
                    CoreMatchers.allOf(
                        childOfViewAtPositionWithMatcher(
                            R.id.tv_Item_Note,
                            LIST_ITEM_ID,
                            withText(list[LIST_ITEM_ID].note)
                        )
                    )
                )
            )

    }

    @Test
    fun testing_recView_clickOnItem() {

        val LIST_ITEM_ID = 1

        onView(withId(R.id.recViewBill)).check(matches(isDisplayed()))

        scenario.onFragment { fragment ->

            fragment.billAdapter.submitList(null)

            fragment.billAdapter.submitList(list)

        }

        //Check action of Item
        onView(withId(R.id.recViewBill))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ShopListViewHolder>(
                    LIST_ITEM_ID,
                    click()
                )
            )

        Assert.assertEquals(navController.currentDestination?.id, R.id.addBillFragment)

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

}