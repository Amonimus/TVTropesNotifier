package com.example.tvtropesnotifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void getRefresh(){
        TextView textbox = this.findViewById(R.id.editTextNumber);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("some_key", "String data");
        setResult(Activity.RESULT_OK, resultIntent);
    }
}