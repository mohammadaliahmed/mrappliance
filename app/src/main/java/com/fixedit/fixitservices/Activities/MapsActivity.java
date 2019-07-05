package com.fixedit.fixitservices.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fixedit.fixitservices.Models.User;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.fixedit.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.BreakIterator;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lng, lat;
    TextView address;
    EditText search;
    ImageView back;
    private Button proceed;
    double origLat, origLon;

    FloatingActionButton takeMeToCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        getPermissions();
        address = findViewById(R.id.address);
        search = findViewById(R.id.search);
        back = findViewById(R.id.back);
        proceed = findViewById(R.id.proceed);
        takeMeToCurrent = findViewById(R.id.takeMeToCurrent);
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyD0ruQXUCNNB5y-bxLyyzy6Qcv9zLc-D_8");

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CommonUtils.showToast("Please turn on GPS");
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            getPermissions();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

//                CommonUtils.showToast( "Place: " + place.getName() + ", " + place.getId());


                LatLng newPosition = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));

                address.setText(lat == 0 ? "" : CommonUtils.getFullAddress(
                        MapsActivity.this, place.getLatLng().latitude, place.getLatLng().longitude));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                CommonUtils.showToast("An error occurred: " + status);
            }
        });
        takeMeToCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng newPosition = new LatLng(origLat, origLon);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));

                address.setText(lat == 0 ? "" : CommonUtils.getFullAddress(MapsActivity.this, origLat, origLon));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
//                try {
//                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .build(MapsActivity.this);
//                    startActivityForResult(intent, 2);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }

            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("address", CommonUtils.getFullAddress(MapsActivity.this, lat, lng));
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lng);
                User user = SharedPrefs.getUser();
                if (user != null) {
                    user.setGoogleAddress(CommonUtils.getFullAddress(MapsActivity.this, lat, lng));
                    user.setLat(lat);
                    user.setLon(lng);
                }
                SharedPrefs.setUser(user);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng mPosition = mMap.getCameraPosition().target;
                float mZoom = mMap.getCameraPosition().zoom;

                lat = mMap.getCameraPosition().target.latitude;
                lng = mMap.getCameraPosition().target.longitude;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        address.setText(lat == 0 ? "" : CommonUtils.getFullAddress(MapsActivity.this, lat, lng));

                    }
                });


            }
        });
    }

    private void getPlaceFromPicker(Intent data) {
//        Log.d(LOG_TAG, "getPlaceFromPicker()");
//        final Place place = PlacePicker.getPlace(this, data);
//        if (place != null && place.getId().length() != 0) {
//            final CharSequence name = place.getName();
//            final CharSequence address = place.getAddress();
//            LatLng newPosition = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));
//
//
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2 ) {
//            getPlaceFromPicker(data);
//            LatLng newPosition = new LatLng(lat, lng);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));
//
//            address.setText(lat == 0 ? "" : CommonUtils.getFullAddress(MapsActivity.this, lat, lng));
//        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                lng = extras.getDouble("Longitude");
                lat = extras.getDouble("Latitude");
                origLat = lat;
                origLon = lng;
                LatLng newPosition = new LatLng(lat, lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));

                address.setText(lat == 0 ? "" : CommonUtils.getFullAddress(MapsActivity.this, lat, lng));


            }

        }
    }

    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (permissions[0].equalsIgnoreCase("android.permission.ACCESS_FINE_LOCATION") && grantResults[0] == 0) {
                Intent intent = new Intent(MapsActivity.this, GPSTrackerActivity.class);
                startActivityForResult(intent, 1);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            getPermissions();
        }

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
//                    CommonUtils.showToast("granted");
                    Intent intent = new Intent(MapsActivity.this, GPSTrackerActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        }
        return true;
    }
}
