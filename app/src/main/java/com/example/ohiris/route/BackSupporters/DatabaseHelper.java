package com.example.ohiris.route.BackSupporters;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static String url = "jdbc:mysql://127.0.0.1:3306/mhealthplay";
    private static String username = "mhealth";
    private static String password = "mhealth";

    private final static String TABLE_NAME_USERS = "myroutesusers";
    private final static String TABLE_NAME_ROUTES = "myroutesroutes";
    private final static String TABLE_NAME_DETAILS = "myroutesdetails";


    public DatabaseHelper() {
        connectToServer();

    }

    private static void connectToServer() {
        String user = "zwd753";
        String password = "Tsubasa530!";
        String host = "murphy.wot.eecs.northwestern.edu";

        int port = 22;

        try {
            Log.d(TAG, "connect to server");
            JSch jsch = new JSch();

            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);

            Properties p = new Properties();
            p.put("StrictHostKeyChecking", "no");
            session.setConfig(p);

            session.connect();
            session.setPortForwardingL(3306, "127.0.0.1", 3306);

            Log.d(TAG, "session Connected");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connectToDB_insertUser(UserAccount userAccount, long id){
        //connectToServer();
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_USERS + " VALUES(?,?,?,?)";

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                PreparedStatement tem = conn.prepareStatement(s);

                //things need to input
                tem.setLong(1, id);
                tem.setString(2, userAccount.getName());
                tem.setString(3, userAccount.getEmail());
                tem.setString(4, userAccount.getPassword());
                Log.d(TAG, "insert user success");

                tem.executeUpdate();

                tem.close();
                conn.close();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void connectToDB_insertRoutes(long userid, int routeid, List<LatLng> list) {
        //connectToServer();
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_ROUTES + " VALUES(?,?,?,?)";

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                int size = list.size();

                for (int i = 0; i < size; i++) {
                    double la = list.get(i).latitude;
                    double lon = list.get(i).longitude;
                    PreparedStatement tem = conn.prepareStatement(s);

                    //things need to input
                    tem.setLong(1, userid);
                    tem.setInt(2, routeid);
                    tem.setDouble(3, la);
                    tem.setDouble(4, lon);
                    Log.d(TAG, "insert route success");

                    tem.executeUpdate();
                    tem.close();
                }


                conn.close();

            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void connectToDB_insertDetails(Route route) throws SQLException{
//        connectToServer();
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_DETAILS + " VALUES(?,?,?,?,?,?,?)";
            int share = 0;
            if (route.getShare()) {
                share = 1;
            }

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {

                PreparedStatement tem = conn.prepareStatement(s);

                //things need to input
                tem.setLong(1,route.getUserId());
                tem.setInt(2, route.getRouteId());
                tem.setString(3, route.getName());
                tem.setLong(4, route.getTime());
                tem.setDouble(5, route.getSpeed());
                tem.setDouble(6, route.getDistance());
                tem.setInt(7, share);

                Log.d(TAG, "insert details success");

                tem.executeUpdate();

                tem.close();
                conn.close();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}
