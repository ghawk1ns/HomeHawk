package com.ghawk1ns.homehawk;

import java.util.Locale;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//parse library
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class Login extends Activity{

    Button btn_LoginIn = null;
	Button btn_SignUp = null;
	Button btn_ForgetPass = null;
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;

	// flag for Internet connection status
	Boolean isInternetPresent = false;
	// Connection detector class
	ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// creating connection detector class instance
		cd = new ConnectionDetector(getApplicationContext());

		btn_LoginIn = (Button) findViewById(R.id.btn_login);
		btn_SignUp = (Button) findViewById(R.id.btn_signup);
		btn_ForgetPass = (Button) findViewById(R.id.btn_ForgetPass);
		mUserNameEditText = (EditText) findViewById(R.id.username);
		mPasswordEditText = (EditText) findViewById(R.id.password);


		btn_LoginIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// get Internet status
				isInternetPresent = cd.isConnectingToInternet();
				// check for Internet status
				if (isInternetPresent) {
					// Internet Connection is Present
					// make HTTP requests
					attemptLogin();
				} else {
					// Internet connection is not present
					// Ask user to connect to Internet
					com.ghawk1ns.homehawk.DialogHandler.showAlertDialog(Login.this, "No Internet Connection",
                            "You don't have internet connection.", false, "ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
				}

			}
		});

		btn_SignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in =  new Intent(Login.this,SignUp.class);
				startActivity(in);
			}
		});
		
		btn_ForgetPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in =  new Intent(Login.this,ForgetParsePassword.class);
				startActivity(in);
			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_forgot_password:
			forgotPassword();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void forgotPassword(){
		/* 
		FragmentManager fm = getSupportFragmentManager();
	     ForgotPasswordDialogFragment forgotPasswordDialog = new ForgotPasswordDialogFragment();
	     forgotPasswordDialog.show(fm, null);
		 */
	}


	public void attemptLogin() {

		clearErrors();

		// Store values at the time of the login attempt.
		String username = mUserNameEditText.getText().toString().trim();
		String password = mPasswordEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			mPasswordEditText.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditText;
			cancel = true;
		} else if (password.length() < 4) {
			mPasswordEditText.setError(getString(R.string.error_invalid_password));
			focusView =mPasswordEditText;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(username)) {
			mUserNameEditText.setError(getString(R.string.error_field_required));
			focusView = mUserNameEditText;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// perform the user login attempt.
			login(username.toLowerCase(Locale.getDefault()), password, false);
		}
	}

	private void login(final String lowerCase, final String password, final boolean fromCache) {
		ParseUser.logInInBackground(lowerCase, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null){
                    loginSuccessful();
                }
				else{
                    loginUnSuccessful();
                }

			}
		});

	}

	protected void loginSuccessful() {
		Intent in =  new Intent(Login.this, MainActivity.class);
        in.putExtra(MainActivity.NEW_USER,true);
		startActivity(in);
	}
	protected void loginUnSuccessful() {
		Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
		com.ghawk1ns.homehawk.DialogHandler.showAlertDialog(this, "Login", "Username or Password is invalid.", false, "ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
	}

	private void clearErrors(){
		mUserNameEditText.setError(null);
		mPasswordEditText.setError(null);
	}






}
