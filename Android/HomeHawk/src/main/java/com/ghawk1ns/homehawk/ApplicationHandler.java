package com.ghawk1ns.homehawk;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by guyhawkins on 2/13/14.
 */

public class ApplicationHandler extends Application
{

    private static ApplicationHandler applicationHandler = new ApplicationHandler();


    @Override
    public void onCreate()
    {
        super.onCreate();
        onCreateParse();

    }

    //prevent creation of new instances
    private void ApplicationManager(){}
    //get singleton instance
    public static ApplicationHandler getInstance(){return applicationHandler;}


    private void onCreateParse() {
        Parse.initialize(this, getResources().getString(R.string.APPLICATION_ID), getResources().getString(R.string.CLIENT_ID));
        //enable push notifications
        PushService.setDefaultPushCallback(this, SplashActivity.class);
    }

}
