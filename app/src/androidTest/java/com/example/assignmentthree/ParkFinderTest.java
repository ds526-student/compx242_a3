package com.example.assignmentthree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JUNIT test for ParkFinder -> checks to see whether it can find parks
 */
@RunWith(AndroidJUnit4.class)
public class ParkFinderTest {
    @Test
    public void TestRequestAndParse(){
        try {
            ParkFinder finder = new ParkFinder(ApplicationProvider.getApplicationContext());

            final boolean[] requestCompleted = {false};

            // placed at the hamilton gardens, parks should always be found if this is functional
            finder.getParks(new LatLng(-37.8057423, 175.3048807), 10000, 10, new ParkFinder.Callback() {
                @Override
                public void onSuccess(java.util.ArrayList<Parks> parks) {
                    requestCompleted[0] = true;
                }

                @Override
                public void onError(Exception e) {
                    requestCompleted[0] = true;
                }
            });

            // wait for request completion
            int waitTime = 0;
            while (!requestCompleted[0] && waitTime < 10000) {
                Thread.sleep(100);
                waitTime += 100;
            }

            // check if request completed successfully
            assertFalse(finder.foundParks.isEmpty());
        }
        catch (Exception e){
            fail();
        }
    }
}