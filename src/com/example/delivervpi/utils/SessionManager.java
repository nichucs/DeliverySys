package com.example.delivervpi.utils;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.delivervpi.LoginActivity;
import com.example.delivervpi.RouteListActivity;

public class SessionManager {

	 // Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Sharedpref file name
    private static final String PREF_NAME = "UserPref";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_GCM = "gcmkey";
    public static final String KEY_USER = "user";
    public static final String KEY_UID = "id";
     
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String name, String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing name in pref
        editor.putString(KEY_USER, name);
         
        // Storing id in pref
        editor.putString(KEY_UID, id);
         
        // commit changes
        editor.commit();
    } 
    /**
     * For Google cloude messaging registration purpose
     * @param key
     */
     public void storeGCMkey(String key){
    	 editor.putString(KEY_GCM, key);
    	 editor.commit();
     }
     public String getGCMkey(){
    	 return pref.getString(KEY_GCM, null);
     }
     
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * @return 
     * */
    public boolean checkLogin(){
        // Check login status
        if(this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, RouteListActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			if(((Activity)_context).getIntent().hasExtra("refresh") && ((Activity)_context).getIntent().getBooleanExtra("refresh", false)){
                i.putExtra("login", true);
			}
            // Staring Login Activity
            _context.startActivity(i);
            return true;
        }else return false;
         
    }
     
     
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USER, pref.getString(KEY_USER, null));
         
        // user email id
        user.put(KEY_UID, pref.getString(KEY_UID, null));
         
        // return user
        return user;
    }
    public String getUid(){
    	return pref.getString(KEY_UID, null);
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
         
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
