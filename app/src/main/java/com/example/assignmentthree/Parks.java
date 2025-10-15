package com.example.assignmentthree;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

public class Parks implements Parcelable {

    public String placeId; // google place id
    public String name; // park name
    public double lat; // park lat location
    public double lng; // park lng location
    public String address; // park address
    public String hours; // park hours
    public ArrayList<String> reviews; // park reviews
    public double rating; // star rating (x/5)

    /**
     * default empty constructor
     */
    public Parks() {}

    /**
     * parses json object data into a parks object
     * @param json json object containing park data
     */
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

    /**
     * get latlng of park
     * @return LatLng object containing park latitude and longitude
     */
    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    /**
     * parcelable constructor to be used by parcel
     * @param in parcel object
     */
    protected Parks(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        rating = in.readDouble();
    }

    /**
     * creates a parcelable creator for parks objects
     */
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

    /**
     * describe contents of parcel
     * @return 0
     */
    @Override
    public int describeContents() { return 0; }

    /**
     * write parcelable object to parcel
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
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
