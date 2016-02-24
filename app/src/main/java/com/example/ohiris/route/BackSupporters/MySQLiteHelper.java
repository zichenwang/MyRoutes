package com.example.ohiris.route.BackSupporters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "MySQLiteHelper";
    private final static int DB_VERSION = 10;
    private final static String DATABASE_NAME = "route.db";
    private final static String TABLE_NAME_USERS = "users";
    private final static String TABLE_NAME_ROUTES = "routes";

    private long userId;

    private final static String CREATE_TABLE_USERS = "create table " + TABLE_NAME_USERS + " (userId Integer primary key autoincrement, " +
            " username text, email text, password text)";

    private final static String CREATE_TABLE_ROUTES = "create table " + TABLE_NAME_ROUTES + " (routeId Integer primary key, " +
            "userId Integer, " + "name text, time Integer, speed real, distance real, share Integer)";


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

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            System.out.println("UPGRADE DB oldVersion=" + oldVersion + " - newVersion=" + newVersion);
            onCreate(sqLiteDatabase);

            if (oldVersion < 10) {
                sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
                sqLiteDatabase.execSQL(CREATE_TABLE_ROUTES);
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

    public UserAccount insertUser(UserAccount queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", queryValues.getName());
        values.put("email", queryValues.getEmail());
        values.put("password", queryValues.getPassword());
        queryValues.setId(database.insert(TABLE_NAME_USERS, null, values));
        database.close();
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

    public UserAccount getUser(String email) {
        String query = "Select userId, password from " + TABLE_NAME_USERS + " where email ='" + email + "'";
        UserAccount myUser = new UserAccount(0, "", "email", "");
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                myUser.setId(cursor.getLong(0));
                myUser.setPassword(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return myUser;
    }

    public int insertPoints(long userid, List<LatLng> list) {
        this.userId = userid;

        SQLiteDatabase db = this.getWritableDatabase();

        if (this.userId > 0) {
            Log.d(TAG, "userId " + userId);
            String CREATE_TABLE_USERS_ROUTES = "create table if not exists " + TABLE_NAME_ROUTES + this.userId + " (routeId Integer, " +
                    " latitude real, longitude real)";
            db.execSQL(CREATE_TABLE_USERS_ROUTES);
        }

        int size = list.size();
        Log.d(TAG, "size of list " + size);

        int routeid = getRoutesNum(this.userId);
        Log.d(TAG, "size of routes " + routeid);

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

        return res;

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
        Log.d(TAG, "number of routes" + id + " " + res);
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
        initialValues.put("distance", route.getDistance());
        initialValues.put("speed", route.getSpeed());
        initialValues.put("share", share);
        Log.d(TAG, initialValues.toString());
        db.insert(TABLE_NAME_ROUTES, null, initialValues);

        int res = testR(route.getRouteId());
        db.close();

       return res;
    }

    public int testR(int rId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from routes where routeId = '" + rId + "'", null);
        mCount.moveToFirst();
        int res = mCount.getInt(0);
        Log.d(TAG, "number of routes" + rId + " " + res);
        mCount.close();
        db.close();

        return res;

    }


    public void deleteAll(Context context) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS users");
//        db.execSQL("DROP TABLE IF EXISTS routes");
//        db.execSQL("D");

        context.deleteDatabase(DATABASE_NAME);
    }

}
