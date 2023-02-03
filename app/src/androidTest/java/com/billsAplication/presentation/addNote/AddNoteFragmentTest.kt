package com.billsAplication.presentation.addNote

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Checks
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddNoteFragmentTest {

    lateinit var scenario: FragmentScenario<AddNoteFragment>

    private val NOTE_KEY = "note"
    private val CREATE_TYPE_NOTE = 10
    private val UPDATE_TYPE = 20
    private val ADD_NOTE_KEY = "add_note_key"
    private val ITEM_NOTE_KEY = "item_note_key"

    private val bundle = Bundle().apply {
        putInt(ADD_NOTE_KEY, CREATE_TYPE_NOTE)
    }

    val item = BillsItem(
        id = 1,
        type = 1,
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
        //Init scenario
        scenario = launchFragmentInContainer(fragmentArgs = bundle,
            themeResId = R.style.Theme_Bills, initialState = Lifecycle.State.STARTED)
        // Create a NavController
        val navController =
            TestNavHostController(context = ApplicationProvider.getApplicationContext())

        scenario.onFragment { fragment: AddNoteFragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Set navigate controller
            Navigation.setViewNavController( view = fragment.requireView(), controller = navController)
            // Then navigate
            navController.navigate(resId = R.id.addNoteFragment)
        }
    }

    @Test
    fun testingTextColor_red(){

            onView(withId(R.id.noteColor_red)).check(matches(isCompletelyDisplayed()))

            onView(withId(R.id.noteColor_red)).perform(click())

            onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.red_a100)))

    }

    @Test
    fun testingTextColor_yellow(){

        onView(withId(R.id.noteColor_yellow)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_yellow)).perform(click())

        onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.yellow_600)))

    }

    @Test
    fun testingTextColor_blue(){

        onView(withId(R.id.noteColor_blue)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_blue)).perform(click())

        onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.blue_a100)))

    }

    @Test
    fun testingTextColor_orange(){

        onView(withId(R.id.noteColor_orange)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_orange)).perform(click())

        onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.orange_600)))

    }

    @Test
    fun testingTextColor_green(){

        onView(withId(R.id.noteColor_green)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_green)).perform(click())

        onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.green_a200)))

    }

    @Test
    fun testingTextColor_purple(){

        onView(withId(R.id.noteColor_purple)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_purple)).perform(click())

        onView(withId(R.id.scrollNote)).check(matches(withBackgroundColor(R.color.deep_purple_a100)))

    }

    @Test
    fun testingTextColor_default(){

        onView(withId(R.id.noteColor_OnPrimary)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.noteColor_OnPrimary)).perform(click())

        scenario.onFragment{ fragment ->

            val scroll = fragment.requireView().findViewById<ScrollView>(R.id.scrollNote)
            val backgroundColor = scroll.background as ColorDrawable

            Assert.assertEquals(fragment.requireContext()
                .getColorFromAttr(com.google.android.material.R.attr.colorBackgroundFloating),
                backgroundColor.color
            )

        }

    }

    @Test
    fun testingEditText_SetText(){

        val str = "Test text"

        onView(withId(R.id.et_addNote)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.et_addNote)).perform(typeText(str), closeSoftKeyboard())

    }

    @Test
    fun testingButtonCancel(){

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

            scenario.onFragment { fragment ->
                //Set Graph in navigate controller
                navController.setGraph(graphResId = R.navigation.navgraph)
                //Go to necessary fragment
                navController.navigate(resId = R.id.addNoteFragment)
                //Set navigate controller
                Navigation.setViewNavController(view = fragment.requireView(), controller = navController)

            }

            onView(withId(R.id.b_cancel_note)).perform(click())

            Assert.assertEquals(navController.currentDestination?.id, R.id.shopListFragment)

    }

    @Test
    fun testingButtonAddNote(){

        val str = "Test text"

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Go to necessary fragment
            navController.navigate(resId = R.id.addNoteFragment)
            //Set navigate controller
            Navigation.setViewNavController(view = fragment.requireView(), controller = navController)

        }

        onView(withId(R.id.et_addNote)).perform(typeText(str), closeSoftKeyboard())

        onView(withId(R.id.b_add_note)).perform(click())

        Assert.assertEquals(navController.currentDestination?.id, R.id.shopListFragment)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testingButtonDelete(){
        //Bundle for update type
        val bundleUpd = Bundle().apply {
            putInt(ADD_NOTE_KEY, UPDATE_TYPE)
            putInt(ITEM_NOTE_KEY, 1)
        }

        scenario.withFragment {
            runTest {
                viewModel.addItem(item = item)
            }
        }

        //Init scenario
        scenario = launchFragmentInContainer(fragmentArgs = bundleUpd,
            themeResId = R.style.Theme_Bills, initialState = Lifecycle.State.STARTED) //CREATE

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

//        scenario.withFragment {
//            runTest {
//                viewModel.addItem(item = item)
//            }
//        }
//
//        scenario.moveToState(newState = Lifecycle.State.RESUMED)

        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Go to necessary fragment
            navController.navigate(resId = R.id.addNoteFragment)
            //Set navigate controller
            Navigation.setViewNavController(view = fragment.requireView(), controller = navController)

        }

        onView(withId(R.id.b_delete_note)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.b_delete_note)).perform(click())

        Assert.assertEquals(navController.currentDestination?.id, R.id.shopListFragment)

    }

    //Utils
    private fun withBackgroundColor(color: Int): Matcher<View?> {
        Checks.checkNotNull(color)
        return object : BoundedMatcher<View?, ScrollView>(ScrollView::class.java) {
            override fun matchesSafely(item: ScrollView): Boolean {
                val backgroundColor = item.background as ColorDrawable
                val colorDrawable = ColorDrawable(ContextCompat.getColor(item.context, color))
                return colorDrawable.color == backgroundColor.color
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
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