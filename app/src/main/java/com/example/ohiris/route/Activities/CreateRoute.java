package com.example.ohiris.route.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.Route;
import com.example.ohiris.route.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateRoute extends AppCompatActivity {
    private static final String TAG = "CreateRoute";

    private EditText et_name;

    private TextView tv_time;
    private TextView tv_dist;
    private TextView tv_speed;

    private Button btn_save;

    private CheckBox checkBox;

    private long userId;
    private int routeId;

    private String name;
    private Boolean share = false;

    private long time;
    private double distance;
    private double speed;

    private Route route;

    private Date date;
    private String dateStr;
    //private SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        userId = extras.getLong("userId");
        routeId = extras.getInt("routeId");
        time = extras.getLong("time");
        time = time / 1000;//seconds

        distance = extras.getDouble("dist");
        speed = extras.getDouble("speed");

        date = new Date();
        dateStr = date.toString();

        et_name = (EditText) findViewById(R.id.editText_route_name);

        tv_time = (TextView) findViewById(R.id.text_time);
        tv_time.setText("" + time + " seconds");

        tv_dist = (TextView) findViewById(R.id.text_dist);
        tv_dist.setText("" + distance + " miles");

        tv_speed = (TextView) findViewById(R.id.text_speed);
        tv_speed.setText("" + speed + " mi/h");

        checkBox = (CheckBox) findViewById(R.id.check_share);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    Toast.makeText(CreateRoute.this, "checked", Toast.LENGTH_SHORT).show();
                    share = true;
                }
            }
        });

        btn_save = (Button) findViewById(R.id.btn_save_route);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = et_name.getText().toString();
                Log.d(TAG, "name of the route: " + name);

                route = new Route(userId, routeId, time, distance, speed, name, share, dateStr);
                MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(CreateRoute.this, userId);
                int res = mySQLiteHelper.createRoute(route);
                Log.d(TAG, "number of entries: " + res);

                finish();

                Intent intent = new Intent(CreateRoute.this, MainAfterLogin.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }
}
