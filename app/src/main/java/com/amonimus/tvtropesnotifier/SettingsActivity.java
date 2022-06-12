package com.amonimus.tvtropesnotifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private GlobalFunctions globalFunctions;
    private SharedPreferences.Editor sharedEditor;
    private BackgroundProcess backgroundProcess;

    private EditText refreshTextbox;
    private TextView currentRefreshLabel;

    private Switch sortSwitch;
    private TextView sortLabel;

    private Switch notifSwitch;
    private TextView notifLabel;

    private int currentRefreshRate;
    private boolean forcedUpdateFlag;

    private TextView loginStatus;
    EditText usernamePrompt;
    EditText passwordPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        globalFunctions = new GlobalFunctions(this);
        backgroundProcess = new BackgroundProcess(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedEditor = sharedPref.edit();

        refreshTextbox = (EditText) this.findViewById(R.id.editTextNumber);
        currentRefreshLabel = this.findViewById(R.id.currentRefreshText);

        sortSwitch = this.findViewById(R.id.sortSwitch);
        sortLabel = this.findViewById(R.id.sortLabel);

        notifSwitch = this.findViewById(R.id.notifSwitch);
        notifLabel = this.findViewById(R.id.notifLabel);

        loginStatus = this.findViewById(R.id.loginStatus);
        usernamePrompt = findViewById(R.id.usernamePrompt);
        passwordPrompt = findViewById(R.id.passwordPrompt);

        TextView creditText = (TextView) findViewById(R.id.creditsView);
        creditText.setMovementMethod(LinkMovementMethod.getInstance());

        updateRefreshLabel();
        updateSort();
        updateNotif();
        refreshLogin();

        refreshTextbox.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!forcedUpdateFlag){
                    String value_str = refreshTextbox.getText().toString();
                    if (value_str.isEmpty()) {
                        refreshTextbox.setText(String.valueOf(0));
                        value_str = refreshTextbox.getText().toString();
                    }
                    currentRefreshRate = Integer.parseInt(value_str);
                    sharedEditor.putInt("STORAGE_REFRESHRATE", currentRefreshRate);
                    sharedEditor.apply();
                    updateRefreshLabel();
                }
            }
        });
    }

    public void registerClick(View view){
        Uri uri = Uri.parse("https://tvtropes.org/pmwiki/login_prompt.php#signup");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void updateRefreshLabel(){
        forcedUpdateFlag = true;
        currentRefreshRate = globalFunctions.getRefreshRate();
        currentRefreshLabel.setText("Current: "+ currentRefreshRate);
        forcedUpdateFlag = false;
    }

    public void registerSortSwitchChange(View view){
        Switch sw = (Switch) view;
        boolean sortSwitchValue = sw.isChecked();
        sharedEditor.putBoolean("STORAGE_SORTING", sortSwitchValue);
        sharedEditor.apply();
        updateSort();
    }

    public void updateSort(){
        boolean check = globalFunctions.getSorting();
        if (check) {
            sortLabel.setText("Latest first");
        } else {
            sortLabel.setText("Earliest first");
        }
        sortSwitch.setChecked(check);
    }

    public void registerNotificationSwitchChange(View view){
        Switch sw = (Switch) view;
        boolean notifSwitchValue = sw.isChecked();
        sharedEditor.putBoolean("STORAGE_NOTIFICATIONS", notifSwitchValue);
        sharedEditor.apply();
        updateNotif();
    }

    public void updateNotif(){
        boolean check = globalFunctions.getNotif();
        if (check) {
            notifLabel.setText("ON");
        } else {
            notifLabel.setText("OFF");
        }
        notifSwitch.setChecked(check);
    }

    public void btnLogin(View view){
        String USERNAME = usernamePrompt.getText().toString();
        String PASSWORD = passwordPrompt.getText().toString();
        sharedEditor.putString("STORAGE_LOGIN", USERNAME);
        sharedEditor.putString("STORAGE_PASSWORD", PASSWORD);
        sharedEditor.apply();
        if (USERNAME.isEmpty() || PASSWORD.isEmpty()){
            loginStatus.setText("Not all fields are filled in.");
        } else {
            backgroundProcess.testLogin();
            refreshLogin();
        }
    }

    public void refreshLogin(){
        usernamePrompt.setText(globalFunctions.getUsername());
        passwordPrompt.setText(globalFunctions.getPassword());
        if (globalFunctions.getSigninStatus()){
            loginStatus.setText("Logged in.");
        } else {
            loginStatus.setText("Login error.");
        }
    }
}