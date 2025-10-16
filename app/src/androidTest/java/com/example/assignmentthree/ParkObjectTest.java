package com.example.assignmentthree;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * JUNIT test that sends incorrect, null, or empty data
 */
public class ParkObjectTest {

    private Parks emptyPark;

    @Before
    public void setUp() {
        emptyPark = new Parks();
    }

    /**
     * test that an empty parks object can be created
     * and that all fields are = to their default values
     */
    @Test
    public void testEmptyParkObjectCreation() {
        Parks park = new Parks();

        assertNull("placeId should be null", park.placeId);
        assertNull("name should be null", park.name);
        assertEquals("lat should be 0.0", 0.0, park.lat, 0.001);
        assertEquals("lng should be 0.0", 0.0, park.lng, 0.001);
        assertNull("address should be null", park.address);
        assertNull("hours should be null", park.hours);
        assertNull("reviews should be null", park.reviews);
        assertEquals("rating should be 0.0", 0.0, park.rating, 0.001);
    }

    /**
     * tests that an empty parks object can be used
     */
    @Test
    public void testEmptyParkObjectUsage() {
        Parks park = new Parks();

        park.name = "Test Park";
        park.placeId = "test_place_id";

        assertEquals("name should be set", "Test Park", park.name);
        assertEquals("placeId should be set", "test_place_id", park.placeId);
    }


    /**
     * tests assigning null values to a park object
     */
    @Test
    public void testParkObjectWithNullValues() {
        Parks park = new Parks();

        park.name = null;
        park.placeId = null;

        assertNull("name should be null", park.name);
        assertNull("placeId should be null", park.placeId);
    }

    /**
     * tests assigning empty string values to a park object
     */
    @Test
    public void testParkObjectWithEmptyValues() {
        Parks park = new Parks();

        park.name = "";
        park.placeId = "";

        assertEquals("name should be empty string", "", park.name);
        assertEquals("placeId should be empty string", "", park.placeId);
        assertTrue("name should be empty", park.name.isEmpty());
    }
}