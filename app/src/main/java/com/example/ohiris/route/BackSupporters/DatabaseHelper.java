package com.example.ohiris.route.BackSupporters;

import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static String url = "jdbc:mysql://127.0.0.1:3306/mhealthplay";
    private static String username = "mhealth";
    private static String password = "mhealth";

    private final static String TABLE_NAME_USERS = "myroutesusers";
    private final static String TABLE_NAME_ROUTES = "routes";


    public DatabaseHelper(){
        connectToServer();

    }

    private static void connectToServer(){
        String user="zwd753";
        String password="Tsubasa530!";
        String host="murphy.wot.eecs.northwestern.edu";

        int port=22;

        try{
            Log.d(TAG, "connect to server");
            JSch jsch=new JSch();

            Session session=jsch.getSession(user, host, port);
            session.setPassword(password);

            Properties p=new Properties();
            p.put("StrictHostKeyChecking", "no");
            session.setConfig(p);


            session.connect();
            session.setPortForwardingL(3306, "127.0.0.1", 3306);

            Log.d(TAG, "session Connected");

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void connectToDB_insertUser(UserAccount userAccount){
        Connection conn;

        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String s = "INSERT INTO " + TABLE_NAME_USERS +" VALUES(?,?,?,?)";

            conn = (Connection) DriverManager.getConnection(url, username, password);

            if (conn != null) {
                PreparedStatement tem = conn.prepareStatement(s);

                //things need to input
                tem.setInt(1, 1);
                tem.setString(2, userAccount.getName());
                tem.setString(3, userAccount.getEmail());
                tem.setString(4, userAccount.getPassword());
                Log.d(TAG,"success");

                tem.executeUpdate();

                tem.close();
                conn.close();

            }
        }catch (Exception e) {
            e.printStackTrace();

        }

    }


}
