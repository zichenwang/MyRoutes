package com.example.ohiris.route.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ohiris.route.BackSupporters.DatabaseHelper;
import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.Route;
import com.example.ohiris.route.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TrakingActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationChangeListener {

    private static final String TAG = "TrakingActivity";

    private static final int MAP_ZOOM = 18; // Google Maps supports 1-21

    private boolean mPermissionDenied = false;

    private static final double MILLISECONDS_PER_HOUR = 1000 * 60 * 60;
    private static final double MILES_PER_KILOMETER = 0.621371192;

    private GoogleMap mMap;

    private LocationManager locationManager;
    private Location currentLocation;
    private Location tempLocation;
    private LatLng currentLatLng;
    private LatLng endLatLng;
    private LatLng tempLatLng;
    private LatLng startLatLng;

    private long startTime;

    private Boolean tracking;

    private long distance;

    private long userId;
    private int routeId;

    private Date startDate;
    private Date endDate;

    private PolylineOptions polylineOptions;

    private List<LatLng> latList = new ArrayList<LatLng>();

    private ShowRoutesTask showRoutesTask;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traking);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userId = extras.getLong("userId");
        routeId = extras.getInt("routeId");
        Toast.makeText(TrakingActivity.this, "" + userId + " " + routeId, Toast.LENGTH_SHORT).show();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {

            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
                        .getMap();
                mMap.setMyLocationEnabled(true);

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setMap();

                    ProgressDialog progressDialog = new ProgressDialog(TrakingActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Getting the route you selected ... ");

                    showRoutesTask = new ShowRoutesTask(userId, routeId, progressDialog, TrakingActivity.this);
                    showRoutesTask.execute((Void) null);
                    progressDialog.show();
                }
            }

            ToggleButton trackingToggleButton = (ToggleButton) findViewById(R.id.trackingToggleButton2);
            tracking = false;
            distance = 0;

            trackingToggleButton.setOnCheckedChangeListener(trackingToggleButtonListener);

        }

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
                        new AlertDialog.Builder(TrakingActivity.this);
                //dialogBuilder.setTitle(R.string.results);

                double distanceKM = distance / 1000.0;
                final double speedKM = distanceKM / totalHours;
                final double distanceMI = distanceKM * MILES_PER_KILOMETER;
                final double speedMI = distanceMI / totalHours;
                final long diff = endDate.getTime() - startDate.getTime();//milliseconds

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:

                                Intent intent = new Intent(TrakingActivity.this, Main2Activity.class);

                                startActivity(intent);

//                                MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(TrakingActivity.this, userId);
//                                mySQLiteHelper.insertPoints(userId, routeId, latList);
//                                Log.d(TAG, "routeid: " + routeId);
//
//                                Intent intent = new Intent(TrakingActivity.this, CreateRoute.class);
//                                intent.putExtra("time", diff);
//                                intent.putExtra("dist", distanceMI);
//                                intent.putExtra("speed", speedMI);
//                                intent.putExtra("routeId", routeId);
//                                intent.putExtra("userId", userId);
//
//                                startActivity(intent);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                Intent intent2 = new Intent(TrakingActivity.this, Main2Activity.class);

                                startActivity(intent2);
                                break;
                        }

                    }
                };

                // display distanceTraveled traveled and average speed
                dialogBuilder.setMessage("Good job! Do you like this route? ").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);

                dialogBuilder.show(); // display the dialog


            } else {
                marker.remove();
                //mMap.clear();
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

    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    public class ShowRoutesTask extends AsyncTask<Void, Void, Boolean> {
        private long uId;
        private int rId;
        private Context context;
        private List<LatLng> list = null;

        private MySQLiteHelper mySQLiteHelper;
        private DatabaseHelper databaseHelper;
        private ProgressDialog progressDialog;


        public ShowRoutesTask(long uId, int rId, ProgressDialog progressDialog, Context context) {
            this.progressDialog = progressDialog;
            this.uId = uId;
            this.rId = rId;
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            mySQLiteHelper = new MySQLiteHelper(this.context);
            mySQLiteHelper.deleteMysqlTable();

            try {
                databaseHelper = new DatabaseHelper();

                Log.d(TAG, "user id: " + uId);

                list = databaseHelper.retrieve_routes(uId, rId);
                Log.d(TAG, "size of the list: " + list.size());

                if (list.size() > 0) {
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);

            if (success) {

                if (list.size() > 0) {
                    drawRoutes(list, 0);
                }
                progressDialog.cancel();

                //mySQLiteHelper.deleteMysqlTable();

            }
        }

    }

    public void drawRoutes(final List<LatLng> list, final int type) {
        //Toast.makeText(Main2Activity.this, "success", Toast.LENGTH_SHORT).show();

//        Color randomColor = colorGenerator();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        Log.d(TAG, "color: " + color);

        //add start and end

        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(color).geodesic(true);

        for (int i = 0; i < list.size(); i++) {
            polylineOptions.add(list.get(i));
        }

        Polyline polyline = mMap.addPolyline(polylineOptions);
        marker = mMap.addMarker(new MarkerOptions().position(list.get(0)).title("START"));
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentLatLng)
//                .zoom(MAP_ZOOM)
//                .bearing(0)
//                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }
}
