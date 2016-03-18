package com.example.ohiris.route.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.DatabaseHelper;
import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.Route;
import com.example.ohiris.route.BackSupporters.UserAccount;
import com.example.ohiris.route.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationChangeListener {
    private static final String TAG = "Main2Activity";


    private static final int MAP_ZOOM = 17; // Google Maps supports 1-21
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private UiSettings mUiSettings;


    private Button create_button;
    private Button recommend_button;
    private Button direct_button;

    private static final String DIRECT_TEXT = "Click the map icon on the right to direct to the start";
    private static final String START_TEXT = "Click here when you ready to start";

    private TextView tv_level;
    private Spinner spinner;

    private long userId;

    private LocationManager locationManager;
    private Location currentLocation;

    private MySQLiteHelper mySQLiteHelper;

    private ShowRoutesTask showRoutesTask = null;
    private GetUserTask getUserTask = null;
    private RecommendTask recommendTask = null;

    private List<Route> routes;

    private Polyline polyline;
    private Polyline flatPolyline;
    private Marker marker;

    private int userNumber;
    private int routesNumber;
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

        routes = new ArrayList<Route>();
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

        spinner = (Spinner) findViewById(R.id.spinner_route_number);
//        tv_level = (TextView) findViewById(R.id.tv_showLevel);
//        tv_level.setVisibility(View.GONE);

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
                    mUiSettings = mMap.getUiSettings();
                    mUiSettings.setZoomControlsEnabled(true);
                    mUiSettings.setZoomGesturesEnabled(true);
                    setMap();
                }
            }

        }

        create_button = (Button) findViewById(R.id.button_create_own);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Map.class);
                intent.putExtra("userId", userId);
                intent.putExtra("routeId", routesNumber);
                startActivity(intent);
            }
        });

        recommend_button = (Button) findViewById(R.id.button_recommend);
        recommend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setRoutes();

                //tv_level.setVisibility(View.GONE);
                direct_button.setVisibility(View.GONE);

                final ProgressDialog progressDialog = new ProgressDialog(Main2Activity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Selecting routes just for you ... ");
                progressDialog.show();
                //clear the map
                mMap.clear();

                //get use details
                getUserTask = new GetUserTask(userId, progressDialog, Main2Activity.this);
                getUserTask.execute((Void) null);

                //get current location

                //choose routes from mysql based on bmi, active level, distance1 and distance 2


            }
        });

        direct_button = (Button) findViewById(R.id.btn_direct);
        direct_button.setVisibility(View.GONE);
        direct_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, TrakingActivity.class);
                startActivity(intent);
            }
        });
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

        flatPolyline = mMap.addPolyline(polylineOptions);
        flatPolyline.setClickable(true);
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                //tv_level.setVisibility(View.VISIBLE);
                direct_button.setVisibility(View.VISIBLE);
                marker = mMap.addMarker(new MarkerOptions().position(list.get(0)).title("START"));
//                if (polyline == flatPolyline){
//                    Log.d(TAG,"JERE");
//                    Toast.makeText(Main2Activity.this, "flat", Toast.LENGTH_LONG);
//                }

//                if (polyline == hillyPolyline){
//
//                }

                //tv_level.setText("Click here when you ready to try");
                if (type == 1) {
                    marker.setSnippet("Level 1: flat");
                    Toast.makeText(getBaseContext(), "Level 1: a flat route", Toast.LENGTH_LONG);
                }

                if (type == 2) {
                    marker.setSnippet("Level 2: hilly");

                    //tv_level.setText("Level 2: hilly");

                    Toast.makeText(getBaseContext(), "Level 2: a hilly route", Toast.LENGTH_LONG);
                }

                if (type == 3) {
                    marker.setSnippet("Level 3: hilly and curved");

                    //tv_level.setText("Level 3: hilly and curved");
                    Toast.makeText(getBaseContext(), "Level 3: a hilly and curved route", Toast.LENGTH_LONG);
                }

            }


        });
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



        if (mMap != null) {
            enableMyLocation();
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(marker!=null){
                        marker.setVisible(false);
                    }

                    //tv_level.setVisibility(View.GONE);
                    direct_button.setVisibility(View.GONE);
                }
            });
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

        ProgressDialog progressDialog = new ProgressDialog(Main2Activity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Trying to show routes around you ... ");

        showRoutesTask = new ShowRoutesTask(userId, progressDialog, Main2Activity.this);
        showRoutesTask.execute((Void) null);
        progressDialog.show();
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
        //tv_level.setVisibility(View.GONE);
        direct_button.setVisibility(View.GONE);
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

    public void forSpinner() {
        int size = routes.size();
        Log.d(TAG, "size of recommended route: " + size);

        List<String> array = new ArrayList<String>();
        array.add("choose one");
        //String[] array = new String[size+1];
        //array[0] = "choose one";
        for (int i = 1; i <= size; i++) {
//            array[i] = "" + i;
            array.add(""+i);
        }

        //Log.d(TAG, "the last number of array: " + array[array.length-1]);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_spinner_item, array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long ids) {

                if (position > 0) {
                    mMap.clear();
                    mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
                    List<LatLng> list = mySQLiteHelper.getRoutesMysql(position-1);
                    drawRoutes(list, routes.get(position-1).getLevel());

                    //tv_level.setVisibility(View.VISIBLE);
                    direct_button.setVisibility(View.VISIBLE);

                    int level = routes.get(position-1).getLevel();
//                    Toast.makeText(Main2Activity.this, "Level 1: a flat route", Toast.LENGTH_LONG);
                    if (level == 1) {
                        //marker.setSnippet("Level 1: flat");
                        Toast.makeText(getBaseContext(), "Level 1: a flat route", Toast.LENGTH_LONG);
                    }

                    if (level == 2) {
                        //marker.setSnippet("Level 2: hilly");

                        //tv_level.setText("Level 2: hilly");

                        Toast.makeText(getBaseContext(), "Level 2: a hilly route", Toast.LENGTH_LONG);
                    }

                    if (level == 3) {
                        //marker.setSnippet("Level 3: hilly and curved");

                        //tv_level.setText("Level 3: hilly and curved");
                        Toast.makeText(getBaseContext(), "Level 3: a hilly and curved route", Toast.LENGTH_LONG);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public class ShowRoutesTask extends AsyncTask<Void, Void, Boolean> {
        private long uId;
        private Context context;

        private MySQLiteHelper mySQLiteHelper;
        private DatabaseHelper databaseHelper;
        private ProgressDialog progressDialog;


        public ShowRoutesTask(long uId, ProgressDialog progressDialog, Context context) {
            this.progressDialog = progressDialog;
            this.uId = uId;
            this.context = context;
            routes.clear();
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            mySQLiteHelper = new MySQLiteHelper(this.context);
            mySQLiteHelper.deleteMysqlTable();

            try {
                databaseHelper = new DatabaseHelper();
                userNumber = databaseHelper.retrieve_userNum();
                Log.d(TAG, "number of users in MySQL: " + userNumber);

                int check = 0;

                for (int i = 1; i <= userNumber; i++) {
                    if ((long) i == uId) {
                        List<Integer> temp = databaseHelper.retrieve_routesNum((long) i);
                        routesNumber = temp.size();



                        //Route r = databaseHelper.retrieve_routesDetail((long) i, listRoute.get(i).getRouteId(), distance, level);

                        continue;
                    }
                    Log.d(TAG, "user id: " + i);
                    List<Integer> rIds = new ArrayList<Integer>();

                    //for each user has a route id list
                    rIds = databaseHelper.retrieve_routesNum((long) i);

                    for (int j = 0; j < rIds.size(); j++) {
//                            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
//                            mySQLiteHelper.storeRoutesFromMysql((long)i, j);

                        List<LatLng> list = databaseHelper.retrieve_routes((long) i, j);
                        Log.d(TAG, "size of the list: " + list.size());

                        if(list.size()>0){
                            //store the start point and ids
                            int startLocs = mySQLiteHelper.insertStartPoints((long) i, j, list.get(0).latitude, list.get(0).longitude);
                            Log.d(TAG, "start locs: " + startLocs);
                        }


                        MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(Main2Activity.this);
                        check = mySQLiteHelper.storeRoutesFromMysql(list, (long) i, j);


                        Route r = databaseHelper.retrieve_routesDetail((long) i, j, 0, 0);
                        routes.add(r);

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

            if (success) {
                //get routes stored in routesmysql
                int size = mySQLiteHelper.getRoutesMysqlNum();
                Log.d(TAG, "how many routes mysql in total " + size);

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        List<LatLng> list = mySQLiteHelper.getRoutesMysql(i);
                        drawRoutes(list, 0);
                    }
                }

                progressDialog.cancel();

                forSpinner();

                //mySQLiteHelper.deleteMysqlTable();

            }
        }
    }


    //get user details
    //calculate bmi and check health status
    public class GetUserTask extends AsyncTask<Void, Void, Boolean> {
        private long uId;
        private Context context;
        private UserAccount userAccount;
        private ProgressDialog progressDialog;

        public GetUserTask(long id, ProgressDialog progressDialog, Context context) {
            this.uId = id;
            this.context = context;
            this.progressDialog = progressDialog;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            DatabaseHelper databaseHelper = new DatabaseHelper();

            try {
                userAccount = databaseHelper.retrieve_userDetails(uId);
                if (userAccount.getActiveLevel() >= 0) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success) {
                userAccount.calBMI();
                boolean res = userAccount.checkHealthy();

                if (res) {//if in healthy range
                    RecommendTask recommendTask = new RecommendTask(userAccount, progressDialog, context, 1);
                    recommendTask.execute((Void) null);
                } else {
                    RecommendTask recommendTask = new RecommendTask(userAccount, progressDialog, context, 0);
                    recommendTask.execute((Void) null);
                }

            } else {
                Log.d(TAG, "can't get user's details");
            }


        }
    }

    //select routes from the mysql
    //store in local database
    //show routes
    public class RecommendTask extends AsyncTask<Void, Void, Boolean> {
        private UserAccount userAccount;
        private Context context;
        private int type;
        private ProgressDialog progressDialog;

        public RecommendTask(UserAccount userAccount, ProgressDialog progressDialog, Context context, int type) {
            this.userAccount = userAccount;
            this.context = context;
            this.type = type;
            this.progressDialog = progressDialog;
            routes.clear();
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(context);
            mySQLiteHelper.deleteMysqlTable();
            Log.d(TAG, "type: " + type);
            //get the routes that is smaller than 5km
            List<Route> listRoute = mySQLiteHelper.getFilteredRoutes(5, currentLocation);

            Log.d(TAG, "number of routes that is in 5 km: " + listRoute.size());

            int distance = 0;
            int level = 0;


            if (type == 1) {
                Log.d(TAG, "healthy");
                //distance from current smaller than 5km,
                //level and distance of route depends on active levels

                level = 3;
                if (userAccount.getActiveLevel() == 0) {
                    distance = 3;
                } else if (userAccount.getActiveLevel() <= 2) {
                    distance = 5;
                } else if (userAccount.getActiveLevel() <= 5) {
                    distance = 20;
                }
            }

            if (type == 0) {
                Log.d(TAG, "unhealthy");
                //distance from current smaller than 5km,
                //level 1 or 2,
                //distance of route depends on active level

                level = 1;

                if (userAccount.getActiveLevel() <= 1) {
                    distance = 3;
                } else if (userAccount.getActiveLevel() <= 3) {
                    distance = 4;
                } else if (userAccount.getActiveLevel() <= 5) {
                    distance = 5;
                }
            }

            DatabaseHelper databaseHelper = new DatabaseHelper();
            Log.d(TAG, "this distance: " + distance + " this level: " + level);

            try {
                for (int i = 0; i < listRoute.size(); i++) {
                    Log.d(TAG, "this user: " + listRoute.get(i).getUserId() + " this route: " + listRoute.get(i).getRouteId());
                    Route r = databaseHelper.retrieve_routesDetail(listRoute.get(i).getUserId(), listRoute.get(i).getRouteId(), distance, level);

                    if (r.getDistance() <= distance && r.getLevel() <= level) {
                        routes.add(r);
                        Log.d(TAG, "level: " + r.getLevel());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                for (int i = 0; i < routes.size(); i++) {
                    List<LatLng> list = databaseHelper.retrieve_routes(routes.get(i).getUserId(), routes.get(i).getRouteId());
                    Log.d(TAG, "size of the route: " + list.size());

                    mySQLiteHelper.storeRoutesFromMysql(list, routes.get(i).getUserId(), routes.get(i).getRouteId());
                }

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mySQLiteHelper = new MySQLiteHelper(context);

            if (success) {
                //get routes stored in routesmysql
                int size = mySQLiteHelper.getRoutesMysqlNum();
                Log.d(TAG, "how many routes mysql in total " + size);

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        List<LatLng> list = mySQLiteHelper.getRoutesMysql(i);
                        drawRoutes(list, 0);
                    }
                }

//                mySQLiteHelper.deleteMysqlTable();

                forSpinner();

                progressDialog.cancel();


            }


        }
    }

}
