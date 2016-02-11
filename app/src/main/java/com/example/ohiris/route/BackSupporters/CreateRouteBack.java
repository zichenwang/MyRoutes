package com.example.ohiris.route.BackSupporters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OhIris on 2/6/16.
 * This class is to receive a list of route and then insert it into database
 */
public class CreateRouteBack {

    private long userId;
    private List<LatLng> list;

    SQLiteDatabase db;

    private MySQLiteHelper mySQLiteHelper;

    public CreateRouteBack(long userId, List<LatLng> list, Context context) {
        this.list = list;
        this.userId = userId;

        mySQLiteHelper = new MySQLiteHelper(context);
    }

    public int insertPoints(long userId) {
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        int size = list.size();
        Log.d("1111111111111", "" + size);

        for (int i = 0; i < size; i++) {
            double la = list.get(i).latitude;
            double lon = list.get(i).longitude;

            ContentValues initialValues = new ContentValues();
            initialValues.put("route_id", "1");
            initialValues.put("langitude", la);
            initialValues.put("longitude", lon);
            Log.d("qqq", initialValues.toString());
            db.insert("routes" + userId, null, initialValues);

        }

        db.close();

        int res = test();

        return res;

    }

    public int test(){
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from routes" + "" + userId + " where routeId='" + "1" + "'", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }



    public List<LatLng> getRoute (long userId) {
        SQLiteDatabase db=mySQLiteHelper.getReadableDatabase();
        String request="select latitude, longitude from routes"+ String.valueOf(userId) +" where route_id='"+"1"+"'";
        Cursor cursor=db.rawQuery(request,new String[]{}); //db.query("points",new String[]{"image_url"},"image_url!='' and route_id='%s'",new String[]{route_id},null,null,null);
        List<LatLng> res=new ArrayList<LatLng>();
        if(cursor.getCount()==0){return res;}
        for(int i=0;i<cursor.getCount(); i++){
            cursor.moveToPosition(i);
            LatLng item=new LatLng(cursor.getDouble(0),cursor.getDouble(1));
            res.add(item);
        }
        cursor.close();
        db.close();
        return res;


    }


}




