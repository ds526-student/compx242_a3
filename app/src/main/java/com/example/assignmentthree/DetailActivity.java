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
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {


    private String placeRequestURL = "https://maps.googleapis.com/maps/api/streetview?size=720x1280&key=AIzaSyB1_c9jegq1TxbJW4CBRDncl5Z4gU3VWbo&location=";
    private String weatherRequestURL = "https://api.weatherapi.com/v1/current.json?key=5b9c77fa145b48fb8c2105358251510&q=";
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

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        Parks park = intent.getParcelableExtra("park");


        if (park != null) {
            // extract lat and lng from park object
            double lat = park.lat;
            double lng = park.lng;

            // create api urls
            loadStreetView(lat, lng);
            getWeatherData(lat, lng);


            // update UI with park data
            TextView tvName = findViewById(R.id.tv_name);
            tvName.setText(park.name);

            TextView tvAddress = findViewById(R.id.tv_address_text);
            tvAddress.setText(park.address != null ? park.address : "Address not available");

            TextView tvHours = findViewById(R.id.tv_hours_text);
            tvHours.setText(park.hours != null ? park.hours : "Hours not available");

            RatingBar ratingBar = findViewById(R.id.rb_ratingBar);
            ratingBar.setRating((float) park.rating);

            TextView tvReviews = findViewById(R.id.tv_reviews_overview);
            tvReviews.setText(String.valueOf(park.rating));
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
}