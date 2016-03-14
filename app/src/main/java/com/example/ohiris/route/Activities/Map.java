package com.example.ohiris.route.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.PolylineOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class Map extends AppCompatActivity implements OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationChangeListener {
    private static final String TAG = "Map";

    private static final int MAP_ZOOM = 18; // Google Maps supports 1-21
    private static final int MAP_BEARING = 180;

    private static final double MILLISECONDS_PER_HOUR = 1000 * 60 * 60;
    private static final double MILES_PER_KILOMETER = 0.621371192;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private LocationManager locationManager;
    private Location currentLocation;
    private Location tempLocation;//previous location

    private LatLng startLatLng;
    private LatLng endLatLng;
    private LatLng currentLatLng;
    private LatLng tempLatLng;

    private ToggleButton tb;

    private Boolean tracking;
    private PolylineOptions polylineOptions;

    private long distance;
    private long startTime;

    private long userId;

    private Date startDate;
    private Date endDate;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private List<LatLng> latList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userId = extras.getLong("userId");
        Toast.makeText(Map.this, "" + userId, Toast.LENGTH_SHORT).show();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {

            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(this);

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setMap();
                }
            }

            ToggleButton trackingToggleButton = (ToggleButton) findViewById(R.id.trackingToggleButton);
            distance = 0;
            tracking = false;

            trackingToggleButton.setOnCheckedChangeListener(trackingToggleButtonListener);

        }


    }


    public void setMap() {

        enableMyLocation();
        mMap.setOnMyLocationButtonClickListener(this);

        if (mMap != null) {
            getMyCurrentLocation();
        }

        if (currentLocation != null) {
            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .zoom(MAP_ZOOM)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(0, 0))
                    .zoom(MAP_ZOOM)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

    }

    public void getMyCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // Finds a provider that matches the criteria
        String provider = locationManager.getBestProvider(criteria, true);
        // Use the provider to get the last known location

        locationManager.requestLocationUpdates(provider, 10000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        currentLocation = locationManager.getLastKnownLocation(provider);

    }

    CompoundButton.OnCheckedChangeListener trackingToggleButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (!isChecked) {//it is not checked
                tracking = false;
                startLatLng = null;
                endLatLng = currentLatLng;
                endDate = new Date();
                if (endLatLng != null) {
                    latList.add(endLatLng);
                }

                mMap.addMarker(new MarkerOptions().position(endLatLng).title("END"));

                // compute the total time we were tracking
                long milliseconds = System.currentTimeMillis() - startTime;
                double totalHours = milliseconds / MILLISECONDS_PER_HOUR;

                // create a dialog displaying the results
                AlertDialog.Builder dialogBuilder =
                        new AlertDialog.Builder(Map.this);
                dialogBuilder.setTitle(R.string.results);

                double distanceKM = distance / 1000.0;
                final double speedKM = distanceKM / totalHours;
                final double distanceMI = distanceKM * MILES_PER_KILOMETER;
                final double speedMI = distanceMI / totalHours;
                final long diff = endDate.getTime() - startDate.getTime();//milliseconds

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:



                                MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Map.this, userId);
                                int res = mySQLiteHelper.insertPoints(userId, latList);
                                Log.d(TAG, "route id: " + res);

                                Intent intent = new Intent(Map.this, CreateRoute.class);
                                intent.putExtra("time", diff);
                                intent.putExtra("dist", distanceMI);
                                intent.putExtra("speed", speedMI);
                                intent.putExtra("routeId", res);
                                intent.putExtra("userId", userId);

                                startActivity(intent);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }

                    }
                };

                // display distanceTraveled traveled and average speed
                dialogBuilder.setMessage("Good job! Do you want to save this route?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                dialogBuilder.show(); // display the dialog


            } else {
                mMap.clear();
                distance = 0;
                latList.clear();

                //start tracking
                startDate = new Date();
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                startLatLng = currentLatLng;//store start location
                tracking = true;
                startTime = System.currentTimeMillis(); // get current time

                mMap.addMarker(new MarkerOptions().position(startLatLng).title("START"));

                endLatLng = null; // starting a new route

                if (currentLatLng != null) {
                    latList.add(currentLatLng);
                }

            }
        }
    };


    @Override
    public void onMyLocationChange(Location location) {

        tempLocation = currentLocation;
        currentLocation = location;

        currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraPosition cameraPosition1 = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(MAP_ZOOM)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

        if (tracking) {

            distance = distance + (long) tempLocation.distanceTo(currentLocation);

            // draw line between location
            polylineOptions = new PolylineOptions().width(10).color(Color.YELLOW).geodesic(true);

            tempLatLng = new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude());
            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            if (currentLatLng != null) {
                latList.add(currentLatLng);
            }

            polylineOptions.add(tempLatLng);
            polylineOptions.add(currentLatLng);

            mMap.addPolyline(polylineOptions);

            //mMap.addMarker(new MarkerOptions().position(currentLatLng).title(currentLatLng.toString()));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .zoom(MAP_ZOOM)
                    .bearing(0)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
            mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            mPermissionDenied = false;
        }
    }

}

