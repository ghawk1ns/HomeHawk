package com.ghawk1ns.homehawk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by guyhawkins on 2/13/14.
 */
public class PrefUtils {
    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__" ;
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__" ;
    public static final String PREFS_USER_NOT_ADDED_TO_NETWORK = "__USERNOTADDEDTONETWORK__" ;

    /**
     * Called to save supplied value in shared preferences against given key.
     * @param context Context of caller activity
     * @param key Key of value to save against
     * @param value Value to save
     */
    public static void saveStringToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static void saveBoolToPrefs(Context context, String key, Boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    /**
     * Called to retrieve required value from shared preferences, identified by given key.
     * Default value will be returned of no value found or error occurred.
     * @param context Context of caller activity
     * @param key Key to find value against
     * @param defaultValue Value to return if no data found against given key
     * @return Return the value found against given key, default if not found or any error occurs
     */
    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * Called to save login credentials
     * @param context Context of caller activity
     * @param username The user's login name
     * @param password The user's password
     * @return void
     */
    public static void saveLoginCredentials(Context context, String username, String password){
        PrefUtils.saveStringToPrefs(context,PrefUtils.PREFS_LOGIN_USERNAME_KEY,username);
        PrefUtils.saveStringToPrefs(context,PrefUtils.PREFS_LOGIN_PASSWORD_KEY,password);
    }

    /**
     * Called to remove login credentials
     * @param context Context of caller activity
     * @return void
     */
    public static void clearLoginCredentials(Context context){
        PrefUtils.saveStringToPrefs(context,PrefUtils.PREFS_LOGIN_USERNAME_KEY,null);
        PrefUtils.saveStringToPrefs(context,PrefUtils.PREFS_LOGIN_PASSWORD_KEY,null);
    }
}