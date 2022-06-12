package com.amonimus.tvtropesnotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class GlobalFunctions {
    private AppCompatActivity activity;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEditor;
    private boolean signedin;

    public GlobalFunctions(AppCompatActivity activity){
        this.activity = activity;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedEditor = sharedPref.edit();
        signedin = false;
    }

    public void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public int getRefreshRate() {
        int REFRESHRATE = sharedPref.getInt("STORAGE_REFRESHRATE", activity.getResources().getInteger(R.integer.STORAGE_REFRESHRATE));
        return REFRESHRATE;
    }

    public boolean getSorting() {
        boolean SORTING = sharedPref.getBoolean("STORAGE_SORTING", false);
        return SORTING;
    }

    public boolean getNotif() {
        boolean NOTIFICATIONS = sharedPref.getBoolean("STORAGE_NOTIFICATIONS", true);
        return NOTIFICATIONS;
    }

    public String getUsername(){
        String USERNAME = sharedPref.getString("STORAGE_LOGIN", activity.getString(R.string.STORAGE_LOGIN));
        return USERNAME;
    }

    public String getPassword(){
        String PASSWORD = sharedPref.getString("STORAGE_PASSWORD", activity.getString(R.string.STORAGE_PASSWORD));
        return PASSWORD;
    }

    public Map getCookies(){
        Map<String, String> sharedcookies = new HashMap<String, String>();
        sharedcookies.put("PHPSESSID", sharedPref.getString("STORAGE_COOKIE_PHPSESSID", ""));
        sharedcookies.put("mylogin", sharedPref.getString("STORAGE_COOKIE_mylogin", ""));
        sharedcookies.put("tvtropes_logged_in", sharedPref.getString("STORAGE_COOKIE_tvtropes_logged_in", ""));
        return sharedcookies;
    }

    public boolean getSigninStatus(){
        signedin = sharedPref.getBoolean("GOT_COOKIE", false);
        return signedin;
    }
}