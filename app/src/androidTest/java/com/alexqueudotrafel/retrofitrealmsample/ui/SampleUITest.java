package com.alexqueudotrafel.retrofitrealmsample.ui;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alexqueudotrafel.retrofitrealmsample.R;
import com.alexqueudotrafel.retrofitrealmsample.activity.MainActivity;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by alexqueudotrafel on 22/03/16.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SampleUITest {

    //For Screengrab
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Check the our listView is not empty after retrieving data from the backend
     * NOPE. We no longer have a listview :)
     */
    /*@Test
    public void listShouldNotBeEmpty(){
        onData(instanceOf(Question.class))
                .inAdapterView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .atPosition(0)
                .check(matches(isDisplayed()));

        // Take screenshots @param: fileName (String)
        Screengrab.screenshot("listView1");
        Screengrab.screenshot("listView2");
    }*/

    @Test
    public void clickOnRecyclerViewItem(){
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Screengrab.screenshot("list1");
        Screengrab.screenshot("list2");
    }

    @Test
    public void swipeToDeleteAndUndoRecyclerViewItem(){
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        Screengrab.screenshot("swipe");

        onView(allOf(withId(android.support.design.R.id.snackbar_action)))
                .check(matches(isDisplayed()))
                .perform(click());
    }
}
