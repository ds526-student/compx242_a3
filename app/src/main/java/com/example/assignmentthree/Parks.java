package com.example.assignmentthree;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Parks {

    public String name;
    public LatLng position;
    public String address;
    public String hours;
    public String weather; // seperate API call -> this will probably be done in a seperate class just here as a placeholder
    public String[] reviews;

    // default constructor not necessary
    Parks() {}

    // custom method to handle serialization of Latlng
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeDouble(position.latitude);
        out.writeDouble(position.longitude);
        out.writeObject(address);
        out.writeObject(hours);
        out.writeObject(weather);
        out.writeObject(reviews);
    }

    // custom method to handle deserialization of Latlng
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        position = new LatLng(in.readDouble(), in.readDouble());
        address = (String) in.readObject();
        hours = (String) in.readObject();
        weather = (String) in.readObject();
        reviews = (String[]) in.readObject();
    }

    Parks(JSONObject park) throws JSONException {
        // get park name
        this.name = park.optString("name", "N/A");

        // get park location
        JSONObject locationInfo = park.getJSONObject("location");
        this.position = new LatLng(locationInfo.getDouble("latitude"), locationInfo.getDouble("longitude"));

        // get park address
        JSONObject addressInfo = park.getJSONObject("addressInfo");
        StringBuilder address = new StringBuilder();
        address.append(addressInfo.optString("Street", " "));
        address.append("\n");
        address.append(addressInfo.optString("State/Province", " "));
        address.append("\n");
        address.append(addressInfo.optString("Town/City", " "));
        address.append("\n");
        address.append(addressInfo.optString("PostalCode", " "));
        address.append("\n");
        address.append(addressInfo.optString("Country", " "));
        this.address = address.toString();

        // get opening hours
        this.hours = park.optString("openingHours", "Opening Hours not available");

        // get reviews
        JSONArray reviewsArray = park.getJSONArray("reviews");
        if (reviewsArray.length() > 0) {
            this.reviews = new String[reviewsArray.length()];
            for (int i = 0; i < reviewsArray.length(); i++) {
                this.reviews[i] = reviewsArray.getString(i);
            }
        }
        else {
            this.reviews = new String[0];
        }

        // get weather -> placeholder for now as this will be done in a seperate class
        this.weather = "Weather data not available";
    }
}
