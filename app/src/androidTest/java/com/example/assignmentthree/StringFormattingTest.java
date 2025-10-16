package com.example.assignmentthree;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUNIT test for strings in detail activity
 */
public class StringFormattingTest {

    /**
     * tests weather string formatting
     */
    @Test
    public void testWeatherStringFormatting() {
        String conditionText = "Sunny";
        double tempCel = 25.5;

        String result = "Temperature: " + tempCel + "°C, " + conditionText;
        String expected = "Temperature: 25.5°C, Sunny";

        assertEquals("Weather string should format correctly", expected, result);
    }

    /**
     * tests weather string formatting with null values
     */
    @Test
    public void testWeatherStringWithNulls() {
        String conditionText = null;
        double tempCel = 0.0;

        String result = conditionText != null ?
                "Temperature: " + tempCel + "°C, " + conditionText :
                "Weather Unavailable";

        assertEquals("Null weather should show unavailable", "Weather Unavailable", result);
    }

    /**
     * tests the address fallback if no valid value is available
     */
    @Test
    public void testAddressFallback() {
        // test with null string address
        String address = null;
        String result = address != null && !address.isEmpty() ? address : "Address not available";

        assertEquals("Null address should show fallback", "Address not available", result);


        // test with empty string address
        address = "";
        result = address != null && !address.isEmpty() ? address : "Address not available";

        assertEquals("Empty address should show fallback", "Address not available", result);
    }

    /**
     * tests rating formatting
     */
    @Test
    public void testRatingDisplay() {
        double rating = 4.5;
        String result = rating + " stars";

        assertEquals("Rating should format with stars", "4.5 stars", result);

        rating = 0.0;
        result = rating + " stars";

        assertEquals("Zero rating should work", "0.0 stars", result);
    }

    /**
     * tests the stringbuilder used to display reviews of parks
     */
    @Test
    public void testReviewStringBuilder() {
        StringBuilder reviewsText = new StringBuilder();

        String author = "John Smith";
        int rating = 5;
        String text = "Great park!";

        reviewsText.append(author)
                .append(" (")
                .append(rating)
                .append(" stars)\n")
                .append(text)
                .append("\n\n");

        String expected = "John Smith (5 stars)\nGreat park!\n\n";
        assertEquals("Review text should build correctly", expected, reviewsText.toString());
    }
}