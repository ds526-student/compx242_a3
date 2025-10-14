package com.example.assignmentthree;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

public class Parks implements Parcelable {

    public String placeId;
    public String name;
    public double lat;
    public double lng;
    public String address;
    public String hours;
    public ArrayList<String> reviews;
    public double rating;

    public Parks() {}

    // passes api response to create a Parks object
    public Parks(JSONObject json) {
        // return if item is null
        if (json == null) return;
        this.placeId = json.optString("place_id", "");
        this.name = json.optString("name", "Unknown");
        this.address = json.optString("address", json.optString("vicinity", ""));
        this.rating = json.optDouble("rating", 0.0);

        // get latlng and parse into separate doubles
        JSONObject geometry = json.optJSONObject("geometry");
        if (geometry != null) {
            JSONObject loc = geometry.optJSONObject("location");
            if (loc != null) {
                this.lat = loc.optDouble("lat", 0.0);
                this.lng = loc.optDouble("lng", 0.0);
            }
        }
    }

    // splits latlng into separate doubles
    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    // defines parcelable object to be passed between activities
    protected Parks(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        rating = in.readDouble();
    }

    // creates a parcelable object
    public static final Creator<Parks> CREATOR = new Creator<Parks>() {
        @Override
        public Parks createFromParcel(Parcel in) {
            return new Parks(in);
        }

        @Override
        public Parks[] newArray(int size) {
            return new Parks[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    // writes parks object data to a parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeDouble(rating);
    }
}
