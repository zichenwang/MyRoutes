package com.example.ohiris.route.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.UserAccount;
import com.example.ohiris.route.R;

public class Signup2 extends AppCompatActivity {
    private static final String TAG = "Signup2";

    private MySQLiteHelper mySQLiteHelper;

    private UserAccount userAccount;
    private int ft;
    private int inch;

    private RadioButton radioButton_male;
    private RadioButton radioButton_female;

    private EditText editText_age;

    private Spinner spinnerft;
    private Spinner spinnerin;

    private EditText editText_weight;

    private SeekBar seekBar;

    private Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        userAccount = new UserAccount();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            userAccount.setName(extras.getString("username"));
            userAccount.setEmail(extras.getString("email"));
            userAccount.setPassword(extras.getString("password"));
            userAccount.setId(extras.getLong("userId"));
        }

        /*****************************************************************************************/

        radioButton_male = (RadioButton) findViewById(R.id.male);
        radioButton_female = (RadioButton) findViewById(R.id.female);

        radioButton_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAccount.setGender("male");
            }
        });

        radioButton_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAccount.setGender("female");
            }
        });

        /*****************************************************************************************/
        editText_age = (EditText) findViewById(R.id.input_age);

        /*****************************************************************************************/
        spinnerft = (Spinner) findViewById(R.id.spinner_ft);
        spinnerin = (Spinner) findViewById(R.id.spinner_in);

        ArrayAdapter<CharSequence> adapterft = ArrayAdapter.createFromResource(
                this, R.array.ft_array, android.R.layout.simple_spinner_item);
        adapterft.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerft.setAdapter(adapterft);
        spinnerft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String ft_str = (String)spinnerft.getSelectedItem();
                ft = Integer.parseInt(ft_str);

                Log.d(TAG, "feet: " + ft);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ft = 4;

            }
        });

        ArrayAdapter<CharSequence> adapterin = ArrayAdapter.createFromResource(
                this, R.array.in_array, android.R.layout.simple_spinner_item);
        adapterin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerin.setAdapter(adapterin);
        spinnerin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String inch_str = (String) spinnerin.getSelectedItem();

                inch = Integer.parseInt(inch_str);

                Log.d(TAG, "inch: " + inch);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                inch = 0;

            }
        });

        /*****************************************************************************************/
        editText_weight = (EditText) findViewById(R.id.input_weight);

        /*****************************************************************************************/
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(0);
        seekBar.setMax(5);
        seekBar.incrementProgressBy(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Log.d(TAG, "level: " + i);
                userAccount.setActiveLevel(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*****************************************************************************************/
        btn_signup = (Button) findViewById(R.id.signup_btn);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double height = ft + inch*0.1;
                Log.d(TAG, "height: " + height);

                userAccount.setHeight(height);
                userAccount.setAge(Integer.parseInt(editText_age.getText().toString()));
                Log.d(TAG, "age: " + Integer.parseInt(editText_age.getText().toString()));
                userAccount.setWeight(Integer.parseInt(editText_weight.getText().toString()));
                Log.d(TAG, "weight: " + Integer.parseInt(editText_weight.getText().toString()));

                mySQLiteHelper = new MySQLiteHelper(Signup2.this);
                userAccount = mySQLiteHelper.insertUser(userAccount);

                Log.d(TAG, "userid: " + userAccount.getId());

                finish();
                Intent intent = new Intent(Signup2.this, Main2Activity.class);
                intent.putExtra("userId", userAccount.getId());
                startActivity(intent);


            }
        });


    }
}
