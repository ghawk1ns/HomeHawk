package com.ghawk1ns.homehawk;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener {
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mCreateAccountButton;

    private String mEmail;
    private String mUsername;
    private String mPassword;
    private String mConfirmPassword;

    private final static String OK = "ok";

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        mUserNameEditText = (EditText) findViewById(R.id.etUsername);
        mEmailEditText = (EditText) findViewById(R.id.etEmail);
        mPasswordEditText = (EditText) findViewById(R.id.etPassword);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.etPasswordConfirm);

        mCreateAccountButton = (Button) findViewById(R.id.btnCreateAccount);
        mCreateAccountButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount:
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
                    createAccount();
                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    DialogHandler.showAlertDialog(getApplicationContext(), "No Internet Connection",
                            "You don't have internet connection.", false, OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    private void createAccount() {
        clearErrors();

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        mEmail = mEmailEditText.getText().toString().trim();
        mUsername = mUserNameEditText.getText().toString().trim();
        mPassword = mPasswordEditText.getText().toString().trim();
        mConfirmPassword = mConfirmPasswordEditText.getText().toString().trim();

        // Check for a valid confirm password.
        if (TextUtils.isEmpty(mConfirmPassword)) {
            mConfirmPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordEditText;
            cancel = true;
        } else if (mPassword != null && !mConfirmPassword.equals(mPassword)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_confirm_password));
            focusView = mPasswordEditText;
            cancel = true;
        }
        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            createUser(mUsername, mEmail, mPassword);
        }

    }


    private void createUser(final String mUsername, String mEmail, String mPassword) {
        final ParseUser user = new ParseUser();
        user.setUsername(mUsername);
        user.setPassword(mPassword);
        user.setEmail(mEmail);

        progress = ProgressDialog.show(this, "Account Creation",
                "Creating Account! Please wait...", true);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    user.signUp();
                    //We're all good, dismiss dialog and launch the main activity
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            signUpMsg("Account Created Successfully");
                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                            in.putExtra(MainActivity.NEW_USER,true);
                            startActivity(in);
                        }
                    });
                } catch (ParseException e) {
                    String message = "An error has occurred, please try again";
                    switch (e.getCode()) {
                        case ParseException.EMAIL_TAKEN:
                            message = "Unfortunately this email has already been taken, please try again";
                            break;
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            message = "Invalid Email address, please try again";
                            break;
                        case ParseException.USERNAME_TAKEN:
                            message = "Unfortunately that username has already been chosen, please try again";
                            break;
                    }
                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            DialogHandler.showAlertDialog(SignUp.this, "Could Not Create Account", finalMessage, false, OK, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }



    //make a toast
    protected void signUpMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void clearErrors() {
        mEmailEditText.setError(null);
        mUserNameEditText.setError(null);
        mPasswordEditText.setError(null);
        mConfirmPasswordEditText.setError(null);
    }

}
