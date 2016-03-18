package com.example.ohiris.route.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.UserAccount;
import com.example.ohiris.route.R;

public class Signup1 extends AppCompatActivity {

    private EditText password1;
    private EditText password2;
    private EditText name;
    private EditText email;
    private TextView backLogin;

    private Button signup_btn;

    private MySQLiteHelper mySQLiteHelper;

    private UserAccount userAccount;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        userId = extras.getLong("userId");

//        if (savedInstanceState == null) {
//            Bundle bundle = getIntent().getExtras();
//
//            if (bundle == null) {
//                eStr = null;
//            } else {
//                eStr = bundle.getString("email");
//            }
//        }else {
//            eStr = (String) savedInstanceState.getSerializable("emails");
//        }

        password1 = (EditText)findViewById(R.id.input_password);
        password2 = (EditText)findViewById(R.id.input_password_confirm);
        name = (EditText)findViewById(R.id.input_name);
        email = (EditText)findViewById(R.id.input_email);

        signup_btn = (Button)findViewById(R.id.btn_signup);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String p1 = password1.getText().toString();
                String p2 = password2.getText().toString();
                String nameStr = name.getText().toString();
                String eStr = email.getText().toString();



                if (!p1.equals(p2)){
                    Toast.makeText(Signup1.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                } else {
//                    userAccount = new UserAccount();
//                    userAccount.setEmail(eStr);
//                    userAccount.setPassword(p1);
//                    userAccount.setName(nameStr);

                    //insert the information in database
                    try {
//                        mySQLiteHelper = new MySQLiteHelper(Signup1.this);
//                        userAccount = mySQLiteHelper.insertUser(userAccount);

//                        long id = userAccount.getId();
                        Intent intent = new Intent(Signup1.this, Signup2.class);
//                        intent.putExtra("userId", id);
                        intent.putExtra("username", nameStr);
                        intent.putExtra("password", p1);
                        intent.putExtra("email", eStr);
                        intent.putExtra("userId", userId);
                        finish();
                        startActivity(intent);
                    } finally {
//                        if (mySQLiteHelper != null) {
//                            mySQLiteHelper.close();
//                        }

                    }

                }
            }
        });

        backLogin = (TextView)findViewById(R.id.link_login);
        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Signup1.this, MainActivity.class));
            }
        });
    }
}
