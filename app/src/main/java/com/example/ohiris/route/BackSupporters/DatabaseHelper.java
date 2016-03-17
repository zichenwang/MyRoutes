package com.example.ohiris.route.BackSupporters;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    //here
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

        //here
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

//    "create table " + TABLE_NAME_USERS + " (userId Integer primary key autoincrement, " +
//            " username text, email text, password text, gender text, age Integer, height real, weight Integer, activelevel Integer)"

    public void connectToDB_insertUser(UserAccount userAccount){
        //connectToServer();
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_USERS + " VALUES(?,?,?,?,?,?,?,?,?)";

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                PreparedStatement tem = conn.prepareStatement(s);

                //things need to input
                tem.setLong(1, userAccount.getId());
                tem.setString(2, userAccount.getName());
                tem.setString(3, userAccount.getEmail());
                tem.setString(4, userAccount.getPassword());
                tem.setString(5, userAccount.getGender());
                tem.setInt(6, userAccount.getAge());
                tem.setDouble(7, userAccount.getHeight());
                tem.setInt(8, userAccount.getWeight());
                tem.setInt(9, userAccount.getActiveLevel());
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

    //    private final static String CREATE_TABLE_ROUTES = "create table " + TABLE_NAME_ROUTES + " (routeId Integer, " +
//            "userId Integer, " + "name text, time Integer, speed real, distance real, date text, level Integer, share Integer)";


    public void connectToDB_insertDetails(Route route) throws SQLException{
//        connectToServer();
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_DETAILS + " VALUES(?,?,?,?,?,?,?,?,?)";
            int share = 0;
            if (route.getShare()) {
                share = 1;
            }

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {

                PreparedStatement tem = conn.prepareStatement(s);

                //things need to input
                tem.setInt(1,route.getRouteId());
                tem.setLong(2, route.getUserId());
                tem.setString(3, route.getName());
                tem.setLong(4, route.getTime());
                tem.setDouble(5, route.getSpeed());
                tem.setDouble(6, route.getDistance());
                tem.setString(7, route.getDate());
                tem.setInt(8, route.getLevel());
                tem.setInt(9, share);

                Log.d(TAG, "insert details success");

                tem.executeUpdate();

                tem.close();
                conn.close();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public int retrieve_userNum() throws SQLException{
        Connection conn;
        int res = 0;

        try{

            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            ResultSet rs = null;

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                String query = "SELECT count(*) FROM myroutesusers";
                Statement statement = conn.createStatement();
                rs = statement.executeQuery(query);

                while(rs.next()){
                    res = rs.getInt(1);
                    Log.d(TAG, "number of users in MySQL: " + res);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return res;
    }

    public List<Integer> retrieve_routesNum(long userId) {
        Connection conn;
        List<Integer> res = new ArrayList<Integer>();

        try{

            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            ResultSet rs = null;

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                String query = "select routeid from myroutesdetails where userid = '"+ userId + "'";
                Statement statement = conn.createStatement();
                rs = statement.executeQuery(query);

                while(rs.next()){
                    int temp = rs.getInt(1);
                    res.add(temp);
                    Log.d(TAG, "one routeid for user " + userId + ": " + temp);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return res;

    }


    public List<LatLng> retrieve_routes(long userId, int routeId) throws SQLException{
        Connection conn;
        List<LatLng> list = new ArrayList<LatLng>();

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            ResultSet rs = null;

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                String query = "SELECT lat, longitude FROM myroutesroutes WHERE userid= '" + userId + "' and routeid = '" + routeId + "'";
                Statement statement = conn.createStatement();
                rs = statement.executeQuery(query);

                while (rs.next()){
                    Double la = rs.getDouble(1);
                    Double lon = rs.getDouble(2);
                    LatLng item = new LatLng(la, lon);
                    list.add(item);
                    Log.d("la", ""+la);
                    Log.d("lon", ""+lon);
                }

                statement.close();
                rs.close();
                conn.close();

            }
        }catch (Exception e) {
            e.printStackTrace();

        }

        return list;
    }

    public UserAccount retrieve_userAccount(String email){
        Connection conn;
        UserAccount userAccount = new UserAccount();

        try{

            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            ResultSet rs = null;

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                String query = "SELECT userid, password FROM myroutesusers where email = '" + email + "'";
                Statement statement = conn.createStatement();
                rs = statement.executeQuery(query);

                while(rs.next()){
                    userAccount.setId(rs.getLong(1));
                    userAccount.setEmail(email);
                    userAccount.setPassword(rs.getString(2));
                    Log.d(TAG, "id: " + userAccount.getId());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return userAccount;
    }




}
