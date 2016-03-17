package com.example.ohiris.route.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.DatabaseHelper;
import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.UserAccount;
import com.example.ohiris.route.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationChangeListener {
    private static final String TAG = "Main2Activity";


    private static final int MAP_ZOOM = 15; // Google Maps supports 1-21
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Button create_button;
    private Button recommend_button;

    private long userId;

    private LocationManager locationManager;
    private Location currentLocation;

    private MySQLiteHelper mySQLiteHelper;

    private ShowRoutesTask showRoutesTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userId = extras.getLong("userId");
        Toast.makeText(Main2Activity.this, "" + userId, Toast.LENGTH_SHORT).show();
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create an instance of GoogleAPIClient.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {

            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1))
                        .getMap();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(this);

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setMap();
                    showRoutesTask = new ShowRoutesTask(userId, Main2Activity.this);
                    showRoutesTask.execute((Void) null);
                }
            }

        }

        create_button = (Button) findViewById(R.id.button_create_own);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Map.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        recommend_button = (Button) findViewById(R.id.button_recommend);
        recommend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setRoutes();

                //clear the map
                mMap.clear();

                //get use details

                //get current location

                //choose routes from mysql based on bmi, active level, distance1 and distance 2



            }
        });
    }

//    public void setRoutes() {
//
//        final long uId = this.userId;
//
//        new AsyncTask<Integer, Void, Void>() {
//            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
//
//            @Override
//            protected Void doInBackground(Integer... params) {
//                try {
//                    databaseHelper = new DatabaseHelper();
//                    int res = databaseHelper.retrieve_userNum();
//                    Log.d(TAG, "number of users in MySQL: " + res);
//
//                    for (int i = 1; i <= res; i++) {
//                        if ((long) i == uId) {
//                            continue;
//                        }
//                        Log.d(TAG, "user id: " + i);
//                        List<Integer> rIds = new ArrayList<Integer>();
//
//                        //for each user
//                        rIds = databaseHelper.retrieve_routesNum((long) i);
//
//                        for (int j = 0; j < rIds.size(); j++) {
////                            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
////                            mySQLiteHelper.storeRoutesFromMysql((long)i, j);
//
//                            List<LatLng> list = databaseHelper.retrieve_routes((long) i, j);
//
////                            drawRoutes(list);
//
//                            Log.d(TAG, "size of the list: " + list.size());
//                            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
//                            mySQLiteHelper.storeRoutesFromMysql(list, (long) i, j);
//
//                        }
//
//                    }
//
//                    //get routes stored in routesmysql
//                    int size = mySQLiteHelper.getRoutesMysqlNum();
//                    Log.d(TAG, "how many routes mysql in total " + size);
//
//                    if (size > 0) {
//                        for (int i = 0; i < size; i++) {
//                            List<LatLng> list = mySQLiteHelper.getRoutesMysql(i);
//                            drawRoutes(list);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute(1);


//        int size = mySQLiteHelper1.getRoutesNum(userId);
//        Log.d(TAG, "how many routes in total " + size);
//
//        for (int i = 0; i < size; i++) {
//            List<LatLng> list = mySQLiteHelper1.getRoutes(userId, ""+ i);
//            drawRoutes(list);
//        }


//    }


    public void drawRoutes(List<LatLng> list) {
        Toast.makeText(Main2Activity.this, "success", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_see_my_routes) {
            Intent intent = new Intent(Main2Activity.this, ShowRoutes.class);
            intent.putExtra("userId", userId);
            startActivity(intent);

        } else if (id == R.id.nav_see_my_profile) {
            Intent intent = new Intent(Main2Activity.this, DeleteActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_log_out) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setMap() {

        enableMyLocation();
        mMap.setOnMyLocationButtonClickListener(this);

        if (mMap != null) {
            getMyCurrentLocation();
        }

        if (currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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
            getMyCurrentLocation();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //getMyCurrentLocation();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            mPermissionDenied = false;
        }
    }

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
                databaseHelper = new DatabaseHelper();
                int res = databaseHelper.retrieve_userNum();
                Log.d(TAG, "number of users in MySQL: " + res);

                int check = 0;

                for (int i = 1; i <= res; i++) {
                    if ((long) i == uId) {
                        continue;
                    }
                    Log.d(TAG, "user id: " + i);
                    List<Integer> rIds = new ArrayList<Integer>();

                    //for each user
                    rIds = databaseHelper.retrieve_routesNum((long) i);

                    for (int j = 0; j < rIds.size(); j++) {
//                            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
//                            mySQLiteHelper.storeRoutesFromMysql((long)i, j);

                        List<LatLng> list = databaseHelper.retrieve_routes((long) i, j);

//                            drawRoutes(list);

                        Log.d(TAG, "size of the list: " + list.size());
                        MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
                        check = mySQLiteHelper.storeRoutesFromMysql(list, (long) i, j);
                    }
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

            if(success){
                //get routes stored in routesmysql
                int size = mySQLiteHelper.getRoutesMysqlNum();
                Log.d(TAG, "how many routes mysql in total " + size);

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        List<LatLng> list = mySQLiteHelper.getRoutesMysql(i);
                        drawRoutes(list);
                    }
                }

                databaseHelper = new DatabaseHelper();
            }
        }
    }


    //get user details
    //calculate bmi and check health status
    public class GetUserTask extends AsyncTask<Void, Void, Boolean>{


        public GetUserTask(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    //select routes from the mysql
    //store in local database
    //show routes
    public class RecommendTask extends AsyncTask<Void, Void, Boolean>{
        private UserAccount userAccount;

        public RecommendTask(){

        }


        @Override
        protected Boolean doInBackground(Void... voids) {





            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
        }
    }

}
