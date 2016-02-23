package com.example.ohiris.route.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.R;

public class DeleteActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        button1 = (Button) findViewById(R.id.btn_delete1);
        button2 = (Button) findViewById(R.id.btn_delete2);
        button3 = (Button) findViewById(R.id.btn_delete3);

        setListener();

    }

    public void setListener(){

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(DeleteActivity.this);
                mySQLiteHelper.deleteAll(DeleteActivity.this);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


}
