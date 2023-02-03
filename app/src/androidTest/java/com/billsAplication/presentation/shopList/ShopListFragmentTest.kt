package com.billsAplication.presentation.shopList

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.billsAplication.R
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.presentation.adapter.shopList.ShopListViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*


@RunWith(AndroidJUnit4::class)
class ShopListFragmentTest {

    lateinit var scenario: FragmentScenario<ShopListFragment>

//    @get:Rule
//    var mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

//    @get:Rule var intentsTestRule = ActivityScenarioRule(MainActivity::class.java)

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

    @Test
    fun visible_AddNote_Button() {

        launchFragmentInContainer(fragmentArgs = Bundle(), themeResId = R.style.Theme_Bills) {
            ShopListFragment()
        }

        onView(withId(R.id.button_addNote))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    @Test
    fun visible_Add_Keyboard_Micro_Button() {

        launchFragmentInContainer(fragmentArgs = Bundle(), themeResId = R.style.Theme_Bills) {
            ShopListFragment()
        }

        onView(withId(R.id.button_addNote)).perform(click())

        onView(withId(R.id.button_addNote_keyboard))
            .check(matches(isDisplayed()))
        onView(withId(R.id.button_addNote_micro))
            .check(matches(isDisplayed()))

    }

    @Test
    fun addButtonNoteKeyboard_goToNoteFragment() {
        val CREATE_TYPE_NOTE = 10
        val ADD_NOTE_KEY = "add_note_key"
        val bundle = Bundle().apply {
            putInt(ADD_NOTE_KEY, CREATE_TYPE_NOTE)
        }


        scenario = launchFragmentInContainer(fragmentArgs = bundle, themeResId = R.style.Theme_Bills)
        scenario.moveToState(newState = Lifecycle.State.STARTED)

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

        scenario.onFragment {
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Go to necessary fragment
            navController.navigate(resId = R.id.shopListFragment)
            //Set navigate controller
            Navigation.setViewNavController(view = it.requireView(), controller = navController)
        }

        onView(withId(R.id.button_addNote))
            .perform(click())

        onView(withId(R.id.button_addNote_keyboard))
            .perform(click())

        Assert.assertEquals(navController.currentDestination?.id, R.id.addNoteFragment)


    }

    @Test
    fun testBackPressed() {
        val CREATE_TYPE_NOTE = 10
        val ADD_NOTE_KEY = "add_note_key"
        val bundle = Bundle().apply {
            putInt(ADD_NOTE_KEY, CREATE_TYPE_NOTE)
        }
        // Create a FragmentScenario
        scenario = FragmentScenario.launchInContainer(
            fragmentClass = ShopListFragment::class.java, fragmentArgs = bundle,
            themeResId = R.style.Theme_Bills, initialState = Lifecycle.State.STARTED)
        // Create a NavController
        val navController =
            TestNavHostController(context = ApplicationProvider.getApplicationContext())

        scenario.onFragment { fragment: ShopListFragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Set navigate controller
            Navigation.setViewNavController( view = fragment.requireView(), controller = navController)
            // Then navigate
            navController.navigate(resId = R.id.shopListFragment)

        }

        onView(withId(R.id.shopListFragment))
            .check(matches(isCompletelyDisplayed()))


        onView(withId(R.id.button_addNote))
            .perform(click())

        onView(withId(R.id.button_addNote_keyboard))
            .perform(click())

        pressBack()

        onView(withId(R.id.shopListFragment))
            .check(matches(isCompletelyDisplayed()))

    }

    @Test
    fun test_recycleView(){

        val list = ArrayList<BillsItem>()
        val LIST_ITEM_ID = 1

        list.add(item)
        list.add(item.copy(id = 2, note = "note 1"))
        list.add(item.copy(id = 3, note = "note 2"))

        scenario = launchFragmentInContainer( fragmentArgs = Bundle(), themeResId = R.style.Theme_Bills)
        scenario.moveToState(newState = Lifecycle.State.STARTED)

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Go to necessary fragment
            navController.navigate(resId = R.id.shopListFragment)
            //Set navigate controller
            Navigation.setViewNavController(view = fragment.requireView(), controller = navController)

            fragment.noteAdapter.submitList(list)

        }

        onView(withId(R.id.recView_shopList)).check(matches(isDisplayed()))

        //Check Contents of Item
        onView(withId(R.id.recView_shopList))
            .check(
                matches(
                    allOf(
                        childOfViewAtPositionWithMatcher(
                            R.id.tv_note,
                            LIST_ITEM_ID,
                            withText(list[LIST_ITEM_ID].note)
                        )
                    )
                )
            )
        //Check action of Item
        onView(withId(R.id.recView_shopList))
            .perform(actionOnItemAtPosition<ShopListViewHolder>(
                LIST_ITEM_ID,
                actionOnChild(
                    click(),
                    R.id.im_RE_note
                )))

        Assert.assertEquals(navController.currentDestination?.id, R.id.addNoteFragment)

    }

    @Test
    fun test_recycleView_GoTo_Links(){
        val list = ArrayList<BillsItem>()
        val LIST_ITEM_ID_FOR_LINKS = 3
        val LINKS = "https://github.com/xabaras/recyclerview-child-actions/blob/master/sample/src/androidTest/java/it/xabaras/android/espresso/recyclerviewchildactions/sample/ChildActionsTest.kt"

        list.add(item)
        list.add(item.copy(id = 2, note = "note 1"))
        list.add(item.copy(id = 3, note = "note 2"))
        list.add(item.copy(id = 4, note = LINKS))

        scenario = launchFragmentInContainer(fragmentArgs = Bundle(), themeResId = R.style.Theme_Bills)
        scenario.moveToState(newState = Lifecycle.State.STARTED)

        val navController = TestNavHostController(
            context = ApplicationProvider.getApplicationContext()
        )

        scenario.onFragment { fragment ->
            //Set Graph in navigate controller
            navController.setGraph(graphResId = R.navigation.navgraph)
            //Go to necessary fragment
            navController.navigate(resId = R.id.shopListFragment)
            //Set navigate controller
            Navigation.setViewNavController(view = fragment.requireView(), controller = navController)

            fragment.noteAdapter.submitList(list)

        }

        Intents.init()
        val expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(LINKS))
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))

        onView(withId(R.id.recView_shopList))
            .perform(actionOnItemAtPosition<ShopListViewHolder>(
                LIST_ITEM_ID_FOR_LINKS,
                click()))

        intended(expectedIntent)
        Intents.release()

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
                val matcher = hasDescendant(allOf(withId(childId), childMatcher))
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
                return allOf(isDisplayed(), isAssignableFrom(View::class.java))
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