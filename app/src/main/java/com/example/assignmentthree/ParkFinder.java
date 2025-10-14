package com.example.assignmentthree;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ParkFinder {

    private Context context;
    public List<Parks> parks;

    public ParkFinder(Context c) {
        parks = new ArrayList<Parks>();
        this.context = c;
    }

    public List<Parks> getParks(LatLng location, int radius, int limit) {
        if (context == null) {
            return null;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();




        return parks;
    }

}
