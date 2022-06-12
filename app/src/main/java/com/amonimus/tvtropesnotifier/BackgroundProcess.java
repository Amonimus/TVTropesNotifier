package com.amonimus.tvtropesnotifier;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

public class BackgroundProcess {
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEditor;
    GlobalFunctions globalFunctions;
    LoginThread loginthread;
    String USERNAME;
    String PASSWORD;
    Map<String, String> saved_cookies;

    public BackgroundProcess(AppCompatActivity activity){
        globalFunctions = new GlobalFunctions(activity);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedEditor = sharedPref.edit();
    }

    public void getCredent(){
        USERNAME = globalFunctions.getUsername();
        PASSWORD = globalFunctions.getPassword();
    }

    public void getCookies(){
        saved_cookies = globalFunctions.getCookies();
    }

    public void testLogin(){
        getCredent();
        loginthread = new LoginThread(USERNAME, PASSWORD);
        loginthread.start();
        try {
            loginthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            sharedEditor.putBoolean("GOT_COOKIE", false);
        }
        if (loginthread.returnValue()){
            Map<String, String> cookies = loginthread.returnCookies();
            for (Map.Entry cookie : cookies.entrySet()){
                Log.d("--TEST", cookie.getKey().toString());
                Log.d("--TEST", cookie.getValue().toString());
                Log.d("--TEST", "STORAGE_COOKIE_"+cookie.getKey().toString());
                sharedEditor.putString("STORAGE_COOKIE_"+cookie.getKey().toString(), cookie.getValue().toString());
                Log.d("--TEST", "SAFE");
            }
            sharedEditor.apply();
        } else {
            Log.d("--TEST", "LOGIN FAIL");
            sharedEditor.putBoolean("GOT_COOKIE", false);
        }
        Log.d("--TEST", "COOKIE SAVED");
        sharedEditor.putBoolean("GOT_COOKIE", true);
        sharedEditor.apply();
        getCookies();
    }

    public List fetch() {
        Log.d("--TEST", String.valueOf(saved_cookies));
        if (globalFunctions.getSigninStatus() && saved_cookies != null) {
            NetThread netthread = new NetThread(USERNAME, PASSWORD, saved_cookies);
            netthread.start();
            try {
                netthread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return netthread.getValue();
        } else {
            return null;
        }
    }
}
