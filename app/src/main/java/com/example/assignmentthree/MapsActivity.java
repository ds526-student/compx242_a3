package com.example.assignmentthree;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.assignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.PlaceAutocomplete;
import com.google.android.libraries.places.widget.PlaceAutocompleteActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLocation; // users current location
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocation; // location provider
    private static final int REQUEST_LOCATION_PERMISSION = 101; // request code
    private ParkFinder pf; // park finder object
    private AutoCompleteTextView searchBar; // search bar for locations
    private Marker searchMarker; // marker for searched location
    private ArrayList<Parks> parksList = new ArrayList<>(); // list of parks

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
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return  insets;
        });

        // sets up places api
        Places.initialize(getApplicationContext(), "null");

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        pf = new ParkFinder(this);

        // gets search bar and initiates search functionality
        searchBar = findViewById(R.id.searchBar);
        initialiseSearchBar();

        getLocation();
    }

    /**
     * initialise search bar and call open autocomplete intent
     */
    private void initialiseSearchBar(){
        searchBar.setFocusable(false);
        searchBar.setClickable(true);
        searchBar.setOnClickListener(v -> startSearch());
    }

    /**
     * open autocomplete intent
     */
    private void startSearch() {
        Intent autocompleteIntent = new PlaceAutocomplete.IntentBuilder().build(this);
        searchLauncher.launch(autocompleteIntent);
    }

    /**
     * gets and returns user location
     */
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

    /**
     * when map is ready, add marker and find nearby parks
     * @param googleMap GoogleMap object
     */
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mMap.addMarker(marker);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null) {
                    onParkClick(marker);
                    return true;
                }
                return false;
            }
        });

        // find nearby parks
        findNearbyParks(latLng);
    }

    /**
     * find nearby parks using ParkFinder
     * @param location the users current location
     */
    private void findNearbyParks(LatLng location) {

        Log.d("MapsActivity", "findNearbyParks called with: " + location.latitude + "," + location.longitude);

        // current location | radius | limit | callback
        pf.getParks(location, 20000, 10, new ParkFinder.Callback() {
            // if successful, add parks to map
            @Override
            public void onSuccess(ArrayList<Parks> parks) {
                Log.d("MapsActivity", "SUCCESS: Received " + parks.size() + " parks");
                parksList = parks;
                runOnUiThread(() -> {
                    for (Parks p : parks) {
                        Log.d("MapsActivity", "Adding park: " + p.name);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(p.getLatLng())
                                .title(p.name)
                                .snippet(p.address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker_location_park)));

                        if (marker != null) {
                            marker.setTag(p);
                        }
                    }
                });
            }

            // if error, log error
            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Log.e("MapsActivity", "Error finding parks", e);
                    Toast.makeText(MapsActivity.this, "Could not find nearby parks.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     *
     * @param requestCode The request code passed in {@link //requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    /**
     * use autocomplete intent to find a location, place a marker, and zoom in
     */
    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // if start activity result is successful, get prediction
                if (result.getResultCode() == PlaceAutocompleteActivity.RESULT_OK && result.getData() != null) {
                    AutocompletePrediction prediction = PlaceAutocomplete.getPredictionFromIntent(result.getData()); // the prediction from the autocomplete intent
                    String placeName = prediction.getPrimaryText(null).toString(); // the name of the place

                    searchBar.setText(placeName); // updates search bar text

                    // get location latlng
                    PlacesClient client = Places.createClient(this);
                    FetchPlaceRequest request = FetchPlaceRequest.builder(
                            prediction.getPlaceId(),
                            Arrays.asList(Place.Field.LOCATION, Place.Field.DISPLAY_NAME)
                    ).build();

                    client.fetchPlace(request).addOnSuccessListener(response -> {
                        Place place = response.getPlace();
                        LatLng location = place.getLocation();

                        if (location != null) {
                            // remove all previous markers
                            if (searchMarker != null) {
                                searchMarker.remove();
                            }

                            // add marker at searched location
                            searchMarker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(placeName)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker_location_searched)));

                            // pan to searched location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                        }
                    // if error, log error
                    }).addOnFailureListener(e -> {
                        Log.e("Search", "Failed to get place location: " + e.getMessage());
                        Toast.makeText(this, "Couldn't find that location", Toast.LENGTH_SHORT).show();
                    });
                }
            });

    /**
     * when a valid park marker is clicked on, start detail activity
     * @param marker the marker to check
     */
    public void onParkClick(Marker marker){
        Log.d("MapsActivity", "onParkClick called with marker title: " + marker.getTitle());
        Log.d("MapsActivity", "Marker position: " + marker.getPosition());
        Log.d("MapsActivity", "Marker tag: " + marker.getTag());
        Log.d("MapsActivity", "Parks list size: " + parksList.size());

        Parks parkObject = (Parks) marker.getTag();

        if (parkObject != null) {
            Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
            intent.putExtra("park", parkObject);
            startActivity(intent);
        }
        else {
            Log.e("MapsActivity", "Park object is null");
        }
    }
}