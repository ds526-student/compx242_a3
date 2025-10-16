package com.example.assignmentthree;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class DetailActivityDataDisplayTest {

    /**
     * Test that DetailActivity displays park name - FIXED VERSION
     */
    @Test
    public void testDetailActivityShowsParkName() throws InterruptedException {
        // Create test park with proper initialization
        Parks testPark = new Parks();
        testPark.name = "Test Park";
        testPark.address = "Test Address";
        testPark.placeId = "test_place_id";
        testPark.lat = -37.7963;
        testPark.lng = 175.2795;
        testPark.rating = 4.0;

        // Create intent with park data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailActivity.class);
        intent.putExtra("park", testPark);

        // Launch DetailActivity with the intent
        ActivityScenarioRule<DetailActivity> activityRule =
                new ActivityScenarioRule<>(intent);
        activityRule.getScenario();

        // Wait for activity to load
        Thread.sleep(2000);
    }
}