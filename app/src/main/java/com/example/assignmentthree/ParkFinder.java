package com.example.assignmentthree;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParkFinder {
    private RequestQueue queue; // to send requests
    private String apiKey = "AIzaSyB1_c9jegq1TxbJW4CBRDncl5Z4gU3VWbo"; // api key to assess api/s

    /**
     * callback interface for getting parks
     */
    public interface Callback {
        void onSuccess(ArrayList<Parks> parks);
        void onError(Exception e);
    }

    /**
     * constructor
     * @param c Context object
     */
    public ParkFinder(Context c) {
        this.queue = Volley.newRequestQueue(c.getApplicationContext());
    }

    /**
     * get parks from api
     * @param location user location
     * @param radius search radius in metres
     * @param limit maximum number of parks to select
     * @param cb callback interface
     */
    public void getParks(LatLng location, int radius, int limit, Callback cb) {
        if (cb == null) return;

        // api url to get parks
        String apiUrl = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=park&key=%s",
                location.latitude, location.longitude, radius, apiKey
        );

        Log.d("ParkFinder", "URL: " + apiUrl);

        // request to get parks
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    // if successful, parse response
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ParkFinder", "Response: " + response.toString());
                        try {
                            ArrayList<Parks> out = parse(response, limit);
                            cb.onSuccess(out);
                        } catch (Exception e) {
                            cb.onError(e);
                        }
                    }
                },
                // if error, log error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ParkFinder", "Error: " + error.getMessage());
                        cb.onError(error);
                    }
                });

        // add request to queue
        queue.add(req);
    }

    /**
     * parse json object into parks array
     * @param json json data to parse
     * @param limit maximum number of parks to select
     * @return an array of parks
     * @throws JSONException if json is invalid
     */
    private ArrayList<Parks> parse(JSONObject json, int limit) throws JSONException {
        ArrayList<Parks> list = new ArrayList<>();
        if (json == null) return list;

        JSONArray results = json.optJSONArray("results"); // "results" not "places"
        if (results == null) return list;

        for (int i = 0; i < results.length() && list.size() < limit; i++) {
            JSONObject item = results.optJSONObject(i);
            if (item == null) continue;
            Parks p = new Parks(item);
            list.add(p);
        }
        return list;
    }
}