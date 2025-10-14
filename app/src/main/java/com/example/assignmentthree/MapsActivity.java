package com.example.assignmentthree;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation; // users current location
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocation; // location provider
    private static final int REQUEST_LOCATION_PERMISSION = 101; // request code
    private ParkFinder pf; // park finder object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        pf = new ParkFinder(this);
        getLocation();
    }

    // gets the users current location
    private void getLocation(){
        // checks if permission is granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // gets last known location
        Task<Location> task = fusedLocation.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            // if successful, set current location and create map
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    // creates map when ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // go to current location and display marker
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title("My Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker_location_current));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.addMarker(marker);

        // find nearby parks
        findNearbyParks(latLng);
    }

    // finds nearby parks
    private void findNearbyParks(LatLng location) {

        Log.d("MapsActivity", "findNearbyParks called with: " + location.latitude + "," + location.longitude);

        // current location | radius | limit | callback
        pf.getParks(location, 50000, 10, new ParkFinder.Callback() {
            // if successful, add parks to map
            @Override
            public void onSuccess(ArrayList<Parks> parks) {
                Log.d("MapsActivity", "SUCCESS: Received " + parks.size() + " parks");
                runOnUiThread(() -> {
                    for (Parks p : parks) {
                        Log.d("MapsActivity", "Adding park: " + p.name);
                        mMap.addMarker(new MarkerOptions()
                                .position(p.getLatLng())
                                .title(p.name)
                                .snippet(p.address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker_location_park)));
                    }
                });
            }

            // if error, log error
            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Log.e("MapsActivity", "Error finding parks", e); // FIXED: import
                    Toast.makeText(MapsActivity.this, "Could not find nearby parks.", Toast.LENGTH_SHORT).show(); // FIXED: import
                });
            }
        });
    }

    // request location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }
}