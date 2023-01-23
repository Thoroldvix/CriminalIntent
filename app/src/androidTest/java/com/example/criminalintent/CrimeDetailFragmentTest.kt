package com.example.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrimeDetailFragmentTest {
    private lateinit var scenario: FragmentScenario<CrimeDetailFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(initialState = Lifecycle.State.RESUMED)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun checkIfCrimeGetsChanged() {

        val title = "Test title"

        onView(withId(R.id.crime_solved))
            .perform(click())
            .check(matches(isChecked()))

        onView(withId(R.id.crime_title))
            .perform(typeText(title))
            .check(matches(withText(title)))

        scenario.onFragment { fragment ->
            assertTrue(fragment.crime.isSolved)
            assertEquals(fragment.crime.title, title)
        }
    }
}