package com.example.ohiris.route.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.DatabaseHelper;
import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowRoutes extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationChangeListener {
    private static final String TAG = "ShowRoutes";

    private long userId;
    private List<LatLng> list;

    private static final int MAP_ZOOM = 18; // Google Maps supports 1-21
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location currentLocation;
    private LatLng currentLatLng;

    private MySQLiteHelper mySQLiteHelper;

    private ShowRoutesTask showRoutesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_routes);

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
                mMap.setOnMyLocationChangeListener(this);

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setMap();
                }
            }

        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        this.userId = extras.getLong("userId");
        Log.d(TAG, "userId " + userId);

//        setRoutes();

        showRoutesTask = new ShowRoutesTask(userId, ShowRoutes.this);
        showRoutesTask.execute((Void) null);
    }

//    public void setRoutes() {
//        mySQLiteHelper = new MySQLiteHelper(getBaseContext());
//
//        int size = mySQLiteHelper.getRoutesNum(userId);
//        Log.d(TAG, "how many routes in total " + size);
//
//        for (int i = 0; i < size; i++) {
//            List<LatLng> list = mySQLiteHelper.getRoutes(userId, ""+ i);
//            drawRoutes(list);
//        }
//    }

    public void drawRoutes(List<LatLng> list) {
        Toast.makeText(ShowRoutes.this, "success", Toast.LENGTH_SHORT).show();

//        Color randomColor = colorGenerator();
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        Log.d(TAG, "color: " + color);

        //add start and end
        mMap.addMarker(new MarkerOptions().position(list.get(0)).title("START"));
        mMap.addMarker(new MarkerOptions().position(list.get(list.size() - 1)).title("END"));


        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(color).geodesic(true);

        for (int i = 0; i < list.size(); i++) {
            polylineOptions.add(list.get(i));
        }

        mMap.addPolyline(polylineOptions);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentLatLng)
//                .zoom(MAP_ZOOM)
//                .bearing(0)
//                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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
                currentLocation = location;
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatLng)
                        .zoom(MAP_ZOOM)
                        .bearing(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

//    public Color colorGenerator(){
//        Random rand = new Random();
//
//        float r = rand.nextFloat();
//        float g = rand.nextFloat();
//        float b = rand.nextFloat();
//
//        Color color = new Color(r, g, b);
//
//        return color;
//    }


    @Override
    public void onMyLocationChange(Location location) {
        currentLocation = location;

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(MAP_ZOOM)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    public class ShowRoutesTask extends AsyncTask<Void, Void, Boolean> {
        private long uId;
        private Context context;

        private MySQLiteHelper mySQLiteHelper;
        private DatabaseHelper databaseHelper;

        public ShowRoutesTask(long uId, Context context) {
            this.uId = uId;
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            mySQLiteHelper = new MySQLiteHelper(this.context);

            try {

                int check = 0;
                databaseHelper = new DatabaseHelper();

                //for each user
                List<Integer> rIds = new ArrayList<Integer>();
                rIds = databaseHelper.retrieve_routesNum(uId);

                for (int j = 0; j < rIds.size(); j++) {
//                            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
//                            mySQLiteHelper.storeRoutesFromMysql((long)i, j);

                    List<LatLng> list = databaseHelper.retrieve_routes(uId, j);

//                            drawRoutes(list);

                    Log.d(TAG, "size of the list: " + list.size());
                    MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(ShowRoutes.this);
                    check = mySQLiteHelper.storeRoutesFromMysql(list, uId, j);
                }


                if (check > 0) {
                    return true;
                } else {
                    return false;
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
                //get routes stored in routesmysql
                int size = mySQLiteHelper.getRoutesMysqlNum();
                Log.d(TAG, "how many routes mysql in total " + size);

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        List<LatLng> list = mySQLiteHelper.getRoutesMysql(i);
                        drawRoutes(list);
                    }
                }

                mySQLiteHelper.deleteMysqlTable();
            }
        }
    }
}
