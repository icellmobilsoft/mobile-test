package com.korbacsk.movies

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MovieListInstrumentedTest {

    var firstActivity: IntentsTestRule<MainActivity>? = null

    @Before
    fun before() {
        if (firstActivity == null) {
            firstActivity = IntentsTestRule(MainActivity::class.java)
            firstActivity?.launchActivity(Intent())

            onView(isRoot()).perform(waitFor(5000))
        }
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun moviesListHasItemTest() {
        // launch desired activity


        val recyclerView: RecyclerView =
            firstActivity!!.getActivity().findViewById(R.id.recyclerViewMovies)
        val itemCount: Int = recyclerView.adapter?.itemCount ?: 0

        assertTrue(
            "recyclerViewMovies adapter itemCount>0",
            itemCount > 0
        )
    }

    @Test
    fun movieDetailsFragmentAddedAndNotEmptyTest() {


        onView(withId(R.id.recyclerViewMovies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        onView(withId(R.id.textViewTitle)).check(matches(not(withText(""))))

    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(delay)
            }

            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }
        }
    }
}
