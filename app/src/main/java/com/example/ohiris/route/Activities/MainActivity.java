package com.example.ohiris.route.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.ohiris.route.BackSupporters.DatabaseHelper;
import com.example.ohiris.route.BackSupporters.MySQLiteHelper;
import com.example.ohiris.route.BackSupporters.UserAccount;
import com.example.ohiris.route.R;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView;
    private TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        signupTextView = (TextView) findViewById(R.id.tosignup_textview);
        signupTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup1.class));
            }
        });
        //mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void test() {
        Log.d(TAG, "test ok");
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;

        private long uId = 0;
        private long newId = 0;

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            //MySQLiteHelper dbHelper = null;
            DatabaseHelper databaseHelper;

            UserAccount userAccount = new UserAccount();

//            dbHelper = new MySQLiteHelper(mContext);
//            myUser = dbHelper.getUser(mEmail);
            try {
                //get the id and password
                if (mEmail != null) {
                    databaseHelper = new DatabaseHelper();
                    userAccount = databaseHelper.retrieve_userAccount(mEmail);
                    uId = userAccount.getId();
                    Log.d(TAG, "userId: " + uId);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            if (uId > 0) {
                try {
                    //find the password and check
                    String passwordreal = userAccount.getPassword();
                    if (passwordreal.equals(mPassword)) {
                        Log.d(TAG, "found");
                        return true;
                    } else {
                        Log.d(TAG, "not found");
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else if (uId == 0) {
                Log.d(TAG, "uid == 0");
                databaseHelper = new DatabaseHelper();
                try {
                    newId = (long) (databaseHelper.retrieve_userNum() + 1);
                    Log.d(TAG, "new id:" + uId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
//
//
//            try {
//
//                if (myUser.getId()>0) {
//                    // Account exists, check password.
//                    if (myUser.getPassword().equals(mPassword))
//                        return true;
//                    else
//                        return false;
//                } else {
//                    myUser.setPassword(mPassword);
//                    return true;
//                }
//
//            } finally {
//            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                if (uId > 0) {
                    finish();
                    Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                    myIntent.putExtra("userId", uId);
                    Toast.makeText(MainActivity.this, "" + uId, Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(myIntent);
                } else if (uId == 0) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //MySQLiteHelper dbTools=null;
                                    try {

                                        test();
//                                        finish();
//                                        dbTools = new MySQLiteHelper(mContext);
//                                        myUser=dbTools.insertUser(myUser);
//                                        Toast myToast = Toast.makeText(mContext,String.valueOf(myUser.getId()), Toast.LENGTH_SHORT);
//                                        myToast.show();
                                        Intent myIntent = new Intent(MainActivity.this, Signup1.class);
                                        myIntent.putExtra("userId", newId);
                                        MainActivity.this.startActivity(myIntent);
                                    } finally {
//                                        if (dbTools!=null)
//                                            dbTools.close();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                                    mPasswordView.requestFocus();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                    builder.setMessage("Email not found. Click CREATE to Sign Up").setPositiveButton("CREATE", dialogClickListener)
                            .setNegativeButton("BACK", dialogClickListener).show();


                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

