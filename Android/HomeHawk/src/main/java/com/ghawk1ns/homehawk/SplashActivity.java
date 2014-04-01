package com.ghawk1ns.homehawk;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class SplashActivity extends ActionBarActivity {

    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(savedInstanceState == null){
            //parse analytics
            ParseAnalytics.trackAppOpened(this.getIntent());
        }
        user = ParseUser.getCurrentUser();
        //Do we have a user for this system?
        if(user == null){
            Intent in =  new Intent(SplashActivity.this, Login.class);
            startActivity(in);
        }
        else{
            Intent in =  new Intent(SplashActivity.this, MainActivity.class);
            startActivity(in);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Probably useless code
     */
//    private void attemptLogin() {
//        String u = PrefUtils.getFromPrefs(this,PrefUtils.PREFS_LOGIN_USERNAME_KEY,null);
//        String p = PrefUtils.getFromPrefs(this,PrefUtils.PREFS_LOGIN_PASSWORD_KEY,null);
//        if(u == null || p == null){
//            Intent in =  new Intent(SplashActivity.this, Login.class);
//            startActivity(in);
//        }
//        else{
//            //attempt to login
//            login(u,p);
//        }
//    }
//
//    private void login(final String username, final String password) {
//        ParseUser.logInInBackground(username, password, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                // TODO Auto-generated method stub
//                if (e == null) {
//                    //if this was not from cache let's save the creds
//                    loginSuccessful();
//                } else {
//                    loginUnSuccessful();
//                }
//
//            }
//        });
//
//    }
//    protected void loginSuccessful() {
//        Intent in =  new Intent(SplashActivity.this, MainActivity.class);
//        startActivity(in);
//    }
//    protected void loginUnSuccessful() {
//        //clear cache because password was not succesful
//        PrefUtils.clearLoginCredentials(this);
//        DialogHandler.showAlertDialog(this,"Login", "Home Hawk Could Not Log You In, Please Login Again.", false,"ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent in =  new Intent(SplashActivity.this, Login.class);
//                startActivity(in);
//            }
//        });
//    }
}
