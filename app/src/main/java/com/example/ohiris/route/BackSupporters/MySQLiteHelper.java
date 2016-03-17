package com.example.ohiris.route.BackSupporters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "MySQLiteHelper";
    private final static int DB_VERSION = 10;
    private final static String DATABASE_NAME = "route.db";
    private final static String TABLE_NAME_USERS = "users";
    private final static String TABLE_NAME_ROUTES = "routes";
    private final static String TABLE_NAME_ROUTES_MYSQL = "routesmysql";

    private long userId;

    private DatabaseHelper databaseHelper;

    private final static String CREATE_TABLE_USERS = "create table " + TABLE_NAME_USERS + " (userId Integer, " +
            "username text, email text, password text, gender text, age Integer, height real, weight Integer, activelevel Integer)";

    private final static String CREATE_TABLE_ROUTES = "create table " + TABLE_NAME_ROUTES + " (routeId Integer, " +
            "userId Integer, " + "name text, time Integer, speed real, distance real, date text, level Integer, share Integer)";

    private final static String CREATE_TABLE_ROUTES_MYSQL = "create table " + TABLE_NAME_ROUTES_MYSQL + " (id Integer, " +
            "userId Integer, routeId Integer, " +
            " latitude real, longitude real)";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);

    }

    public MySQLiteHelper(Context context, long userId) {
        super(context, "route.db", null, DB_VERSION);


        this.userId = userId;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_ROUTES);
        sqLiteDatabase.execSQL(CREATE_TABLE_ROUTES_MYSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            System.out.println("UPGRADE DB oldVersion=" + oldVersion + " - newVersion=" + newVersion);
            onCreate(sqLiteDatabase);

            if (oldVersion < 10) {
                sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
                sqLiteDatabase.execSQL(CREATE_TABLE_ROUTES);
                sqLiteDatabase.execSQL(CREATE_TABLE_ROUTES_MYSQL);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // super.onDowngrade(db, oldVersion, newVersion);
        System.out.println("DOWNGRADE DB oldVersion=" + oldVersion + " - newVersion=" + newVersion);
    }

//    private final static String CREATE_TABLE_USERS = "create table " + TABLE_NAME_USERS + " (userId Integer, " +
//            " username text, email text, password text, gender text, age Integer, height real, weight Integer, activelevel Integer)";
    public UserAccount insertUser(UserAccount queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", queryValues.getId());
        values.put("username", queryValues.getName());
        values.put("email", queryValues.getEmail());
        values.put("password", queryValues.getPassword());
        values.put("gender", queryValues.getGender());
        values.put("age", queryValues.getAge());
        values.put("height", queryValues.getHeight());
        values.put("weight", queryValues.getWeight());
        values.put("activelevel", queryValues.getActiveLevel());
        Log.d(TAG, values.toString());

        database.insert(TABLE_NAME_USERS, null, values);
        database.close();

        final UserAccount userAccount = queryValues;
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    databaseHelper = new DatabaseHelper();
                    databaseHelper.connectToDB_insertUser(userAccount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);



        return queryValues;
    }

    public int updateUserPassword(UserAccount queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.getName());
        values.put("password", queryValues.getPassword());
        queryValues.setId(database.insert(TABLE_NAME_USERS, null, values));
        database.close();
        return database.update(TABLE_NAME_USERS, values, "userId = ?", new String[]{String.valueOf(queryValues.getId())});
    }


//    public UserAccount getUser(long uId) {
//        String query = "Select userId, password from " + TABLE_NAME_USERS + " where email ='" + email + "'";
//        UserAccount myUser = new UserAccount(0, "", "email", "");
//        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor cursor = database.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                myUser.setId(cursor.getLong(0));
//                myUser.setPassword(cursor.getString(1));
//            } while (cursor.moveToNext());
//        }
//        return myUser;
//    }

    public int insertPoints(final long userid, List<LatLng> list) {
        this.userId = userid;

        SQLiteDatabase db = this.getWritableDatabase();

        if (this.userId > 0) {
            Log.d(TAG, "userId " + userId);
            String CREATE_TABLE_USERS_ROUTES = "create table if not exists " + TABLE_NAME_ROUTES + this.userId + " (routeId Integer, " +
                    " latitude real, longitude real)";
            db.execSQL(CREATE_TABLE_USERS_ROUTES);
        }

        int size = list.size();
        Log.d(TAG, "size of Lat list " + size);

        int routeid = getRoutesNum(this.userId);

        db = this.getWritableDatabase();

        for (int i = 0; i < size; i++) {
            double la = list.get(i).latitude;
            double lon = list.get(i).longitude;

            ContentValues initialValues = new ContentValues();
            initialValues.put("routeId", routeid);
            initialValues.put("latitude", la);
            initialValues.put("longitude", lon);
            Log.d(TAG, initialValues.toString());
            db.insert("routes" + userId, null, initialValues);
        }

        int res = test(routeid);
        db.close();

        final long useridDB = userid;
        final int routeidDB =routeid;
        final List<LatLng> listDB = list;
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {

                    databaseHelper = new DatabaseHelper();
                    databaseHelper.connectToDB_insertRoutes(useridDB, routeidDB, listDB);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);


        return routeid;

    }

    public int test(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from routes" + "" + this.userId + " where routeId= '" + id + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        Log.d(TAG, "number of points in routes" + userId + " " + count);
        mCount.close();
        db.close();

        return count;
    }

    public int getRoutesNum(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + "(select distinct routeId from routes" + "" + id + " )", null);
        mCount.moveToFirst();
        int res = mCount.getInt(0);
        Log.d(TAG, "get routes id: " + res);
        mCount.close();
        db.close();

        return res;
    }

    public List<LatLng> getRoutes(long uId, String rId) {

        List<LatLng> list = new ArrayList<LatLng>();

        SQLiteDatabase db = this.getReadableDatabase();

        String request = "select latitude, longitude from routes" + "" + uId + " where routeId='" + rId + "'";
        Cursor cursor = db.rawQuery(request, new String[]{});

        if (cursor.getCount() == 0) {
            return list;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            LatLng item = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            list.add(item);
            Log.d(TAG, item.toString());
        }
        cursor.close();
        db.close();

        return list;
    }

//    private final static String CREATE_TABLE_ROUTES = "create table " + TABLE_NAME_ROUTES + " (routeId Integer, " +
//            "userId Integer, " + "name text, time Integer, speed real, distance real, date text, level Integer, share Integer)";

    public int createRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();

        int share = 0;
        if (route.getShare()) {
            share = 1;
        }

        ContentValues initialValues = new ContentValues();
        initialValues.put("routeId", route.getRouteId());
        initialValues.put("userId", route.getUserId());
        initialValues.put("name", route.getName());
        initialValues.put("time", route.getTime());
        initialValues.put("speed", route.getSpeed());
        initialValues.put("distance", route.getDistance());
        initialValues.put("share", share);
        initialValues.put("date", route.getDate());
        initialValues.put("level", route.getLevel());

        Log.d(TAG, initialValues.toString());
        db.insert(TABLE_NAME_ROUTES, null, initialValues);

        int res = testR(route.getUserId());
        db.close();

        final Route routeDB = route;
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    databaseHelper = new DatabaseHelper();
                    databaseHelper.connectToDB_insertDetails(routeDB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);

        return res;
    }

    public int testR(long uId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from routes where userId = '" + uId + "'", null);
        mCount.moveToFirst();
        int res = mCount.getInt(0);
        Log.d(TAG, "user " + uId + " have " + res + " routes recorded");
        mCount.close();
        db.close();

        return res;

    }

//    private final static String CREATE_TABLE_ROUTES_MYSQL = "create table " + TABLE_NAME_ROUTES_MYSQL + " (id Integer, userId Integer, routeId Integer, " +
//            " latitude real, longitude real)";

    public int storeRoutesFromMysql(List<LatLng> list, long uId, int rId){

        int size = list.size();
        Log.d(TAG, "size of Lat list " + size);


        int id = getRoutesMysqlNum();
        Log.d(TAG, "id of this route: " + id);

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < size; i++) {
            double la = list.get(i).latitude;
            double lon = list.get(i).longitude;

            ContentValues initialValues = new ContentValues();
            initialValues.put("id", id);
            initialValues.put("userId", uId);
            initialValues.put("routeId", rId);
            initialValues.put("latitude", la);
            initialValues.put("longitude", lon);
            Log.d(TAG, initialValues.toString());

            db.insert("routesmysql", null, initialValues);

        }

        db.close();

        return getRoutesMysqlNum();

    }

    public int getRoutesMysqlNum(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + "(select distinct id from routesmysql)", null);
        mCount.moveToFirst();
        int res = mCount.getInt(0);
        Log.d(TAG, "how many routes: " + res);
        mCount.close();
        db.close();

        return res;
    }

    public List<LatLng> getRoutesMysql(int id){

        List<LatLng> list = new ArrayList<LatLng>();

        SQLiteDatabase db = this.getReadableDatabase();

        String request = "select latitude, longitude from routesmysql where id='" + id + "'";
        Cursor cursor = db.rawQuery(request, new String[]{});

        if (cursor.getCount() == 0) {
            return list;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            LatLng item = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            list.add(item);
            Log.d(TAG, "mysql: " + item.toString());
        }
        cursor.close();
        db.close();

        return list;
    }


    /*****************delete**************/


    public void deleteAll(Context context) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS users");
//        db.execSQL("DROP TABLE IF EXISTS routes");
//        db.execSQL("D");

        context.deleteDatabase(DATABASE_NAME);
    }

    public void deleteMysqlTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_ROUTES_MYSQL);
        db.close();
    }

//    public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
//        private static final String TAG = "CreateUserTask";
//        private final UserAccount userAccount;
//
//        public CreateUserTask (UserAccount userAccount){
//            this.userAccount = userAccount;
//
//        }
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//
//            try {
//                databaseHelper = new DatabaseHelper();
//                databaseHelper.connectToDB_insertUser(userAccount);
//                Log.d(TAG, "hereconnect");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return true;
//
//
//        }
//    }

}
