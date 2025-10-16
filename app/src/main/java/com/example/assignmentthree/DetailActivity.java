package com.example.assignmentthree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private final Integer MAX_REVIEWS = 5;
    private String placeRequestURL = "https://maps.googleapis.com/maps/api/streetview?size=720x1280&key=AIzaSyB1_c9jegq1TxbJW4CBRDncl5Z4gU3VWbo&location=";
    private String weatherRequestURL = "https://api.weatherapi.com/v1/current.json?key=5b9c77fa145b48fb8c2105358251510&q=";
    private String informationRequestURL = "https://maps.googleapis.com/maps/api/place/details/json?place_id=";

    private RequestQueue requestQueue;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // hide system bars.
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        Parks park = intent.getParcelableExtra("park");


        if (park != null) {
            // extract lat and lng from park object
            double lat = park.lat;
            double lng = park.lng;

            // call apis and update related UI elements
            loadStreetView(lat, lng);
            getWeatherData(lat, lng);
            getSpecificHours(park.placeId);
            getReviews(park.placeId);


            // update UI with park data
            TextView tvName = findViewById(R.id.tv_name);
            tvName.setText(park.name);

            TextView tvAddress = findViewById(R.id.tv_address_text);
            tvAddress.setText(park.address != null ? park.address : "Address not available");

            RatingBar ratingBar = findViewById(R.id.rb_ratingBar);
            ratingBar.setRating((float) park.rating);

            TextView tvReviews = findViewById(R.id.tv_reviews_overview);
            tvReviews.setText(park.rating + " stars");
        }
    }

    /**
     * load a street view image into the image view
     * @param lat Latitude of the park
     * @param lng Longitude of the park
     */
    private void loadStreetView(double lat, double lng) {
        String streetViewUrl = placeRequestURL + lat + "," + lng;

        ImageView ivDisplay = findViewById(R.id.iv_display);

        Glide.with(this)
                .load(streetViewUrl)
                .into(ivDisplay);
    }

    /**
     * gets weather data from weather api
     * @param lat Latitude of the park
     * @param lng Longitude of the park
     */
    private void getWeatherData(double lat, double lng) {
        String weatherUrl = weatherRequestURL + lat + "," + lng;

        JsonObjectRequest weatherRequest = new JsonObjectRequest(
            Request.Method.GET,
            weatherUrl,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // try to select the condition text and current temp_c from weather api response
                        JSONObject current = response.getJSONObject("current");
                        JSONObject condition = current.getJSONObject("condition");

                        String conditionText = condition.getString("text");
                        double tempCel = current.getDouble("temp_c");

                        Log.d("Weather", "Parsed - Condition: " + conditionText + ", Temp: " + tempCel);
                        updateWeatherUI(conditionText, tempCel);

                    } catch (Exception e) {
                        Log.e("Weather", "Error parsing weather JSON: " + e.getMessage());
                        updateWeatherUI("Weather Unavailable", 0);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Weather", "Weather API error: " + error.toString());
                    updateWeatherUI("Weather Unavailable", 0);
                }
            }
        );

        // add request to request queue
        requestQueue.add(weatherRequest);
    }

    /**
     * update UI with weather data
     * @param conditionText Condition text -> cloudy, sunny, etc.
     * @param tempCel Temperature in celsius
     */
    private void updateWeatherUI(String conditionText, double tempCel) {
        TextView tvWeather = findViewById(R.id.tv_weather_text);

        Log.d("Weather", "conditionText: " + conditionText);
        Log.d("Weather", "tempCel: " + tempCel);

        // if weather is unavailable, display "Weather Unavailable"
        if (conditionText.equals("Weather Unavailable")) {
            tvWeather.setText(conditionText);
        }
        else {
            tvWeather.setText("Temperature: " + tempCel + "°C, " + conditionText);
        }
    }

    /**
     * gets detailed hours(opening and closing) from google places api
     * current hours only specify whether a location is currently open or closed
     * @param placeID Google Place ID of the park
     */
    private void getSpecificHours(String placeID) {
        if (placeID == null || placeID.isEmpty()) {
            Log.e("Hours", "Invalid placeID: " + placeID);
            updateHoursUI("Hours Unavailable");
            return;
        }

        String hoursUrl = informationRequestURL + placeID + "&fields=opening_hours" + "&key=AIzaSyB1_c9jegq1TxbJW4CBRDncl5Z4gU3VWbo";
        Log.d("Hours", "URL: " + hoursUrl);

        JsonObjectRequest hoursRequest = new JsonObjectRequest(
            Request.Method.GET,
            hoursUrl,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Hours", "Response: " + response.toString());
                    try {
                        JSONObject result = response.getJSONObject("result");

                        if (result.has("opening_hours")) {
                            JSONObject openingHours = result.getJSONObject("opening_hours");

                            if (openingHours.has("weekday_text")) {
                                JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                                StringBuilder hoursText = new StringBuilder();

                                for (int i = 0; i < weekdayText.length(); i++) {
                                    hoursText.append(weekdayText.getString(i));

                                    // new line in between each day other than sunday
                                    if (i < weekdayText.length() - 1){
                                        hoursText.append("\n");
                                    }
                                }

                                updateHoursUI(hoursText.toString());
                            }
                            else {
                                updateHoursUI("Hours Unavailable");
                            }
                        }
                        else {
                            updateHoursUI("Hours Unavailable");
                        }

                    } catch (Exception e) {
                        Log.e("Hours", "Error parsing hours JSON: " + e.getMessage());
                        updateHoursUI("Hours Unavailable");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Hours", "Hours API error: " + error.toString());
                    updateHoursUI("Hours Unavailable");
                }
            }
        );
        requestQueue.add(hoursRequest);
    }

    /**
     * update UI with hours data
     * @param hours Hours data in String format
     */
    private void updateHoursUI(String hours) {
        Log.d("Hours", "hours: " + hours);
        TextView tvHours = findViewById(R.id.tv_hours_text);

        if (hours == null || hours.isEmpty()) {
            hours = "Hours Unavailable";
        }

        tvHours.setText(hours);
    }

    /**
     * gets 5 reviews from google places api
     * @param placeID Google Place ID of the park
     */
    private void getReviews(String placeID) {
        if (placeID == null || placeID.isEmpty()) {
            Log.e("Reviews", "Invalid placeID: " + placeID);
            updateReviewsUI("Hours Unavailable");
            return;
        }

        String reviewsUrl = informationRequestURL + placeID + "&fields=reviews" + "&key=AIzaSyB1_c9jegq1TxbJW4CBRDncl5Z4gU3VWbo";
        Log.d("Reviews", "URL: " + reviewsUrl);

        JsonObjectRequest reviewsRequest = new JsonObjectRequest(
            Request.Method.GET,
            reviewsUrl,
            null,
                new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Reviews", "Response: " + response.toString());
                    try {
                        JSONObject result = response.getJSONObject("result");

                        if (result.has("reviews")) {
                            JSONArray reviews = result.getJSONArray("reviews");
                            StringBuilder reviewsText = new StringBuilder();

                            int reviewCount = Math.min(reviews.length(), MAX_REVIEWS);
                            for (int i = 0; i < reviewCount; i++) {
                                JSONObject review = reviews.getJSONObject(i);

                                String author = review.optString("author_name", "Anonymous");
                                int rating = review.optInt("rating", 0);
                                String text = review.optString("text", "");

                                reviewsText.append(author)
                                        .append(" (")
                                        .append(rating)
                                        .append(" stars)\n")
                                        .append(text)
                                        .append("\n\n");
                            }
                            updateReviewsUI(reviewsText.toString());
                        }
                        else {
                            updateReviewsUI("No reviews available");
                        }

                    } catch (Exception e) {
                        Log.e("Reviews", "Error parsing reviews JSON: " + e.getMessage());
                        updateReviewsUI("No reviews available");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Reviews", "Reviews API error: " + error.toString());
                    updateReviewsUI("No reviews available");
                }
            }
        );
        requestQueue.add(reviewsRequest);
    }

    /**
     * update UI with reviews data
     * @param reviews Reviews data in String format
     */
    private void updateReviewsUI(String reviews) {
        if (reviews == null || reviews.isEmpty()) {
            reviews = "No reviews available";
        }

        TextView tvReviews = findViewById(R.id.tv_reviews_text);
        tvReviews.setText(reviews);
    }


}