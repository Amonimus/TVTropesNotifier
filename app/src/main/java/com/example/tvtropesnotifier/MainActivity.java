package com.example.tvtropesnotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("TVTN Notification", "TVTN Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        loopTask();
    }

    int refresh_rate = 1;
    BackgroundProcess background = new BackgroundProcess();
    MessageActions actions = new MessageActions();
    Thread repeatTaskThread;

    public void loopTask() {
        repeatTaskThread = new Thread() {
            public void run() {
                while (true) {
                    if (refresh_rate < 1) {
                        try {
                            Thread.sleep(refresh_rate * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fetchMessages();
                            }
                        });
                        try {
                            Thread.sleep(refresh_rate * 60000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        repeatTaskThread.start();
    }

    public void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void clearMessages() {
        LinearLayout container = this.findViewById(R.id.messageContainer);
        container.removeAllViews();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.cancelAll();
    }

    //Button actions
    public void openMenu(View view) {
        Intent intent = new Intent(this, SecondWindow.class);
        //EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        //String message = editText.getText().toString();
        //intent.putExtra(CROSSACTIVITY_MESSAGE, message);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logoButtonClick(View view){
        openLink("https://tvtropes.org/");
    }

    public void datesorting(List list){
        DateFormat f = new SimpleDateFormat("MMM dd yyyy, hh:mm aa");
        Collections.sort(list, new Comparator<Map<String, String>>(){
            @Override
            public int compare(Map<String, String> object1, Map<String, String> object2) {
                try {
                    return f.parse(dateConverter(object1.get("timeStamp"))).compareTo(f.parse(dateConverter(object2.get("timeStamp"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    public String dateConverter(String timeStamp){
        timeStamp = timeStamp.replace("st", "").replace("nd", "").replace("rd", "").replace("th", "");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
        Date date = null;
        try {
            date = sdf.parse(timeStamp);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat("MMM dd hh:mm");
            try {
                date = sdf.parse(timeStamp);
            } catch (ParseException ex) {
                sdf = new SimpleDateFormat("dd MMM hh:mm aa");
                try {
                    date = sdf.parse(timeStamp);
                } catch (ParseException exp) {
                    exp.printStackTrace();
                    return timeStamp;
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == 1970){
            cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        }
        DateFormat formatter = new SimpleDateFormat("MMM dd yyyy, hh:mm aa");
        String convertedDate = formatter.format(cal.getTime());
        return convertedDate;
    }

    public void fetchMessages(){
        clearMessages();
        List<Map> data = background.fetch();
        datesorting(data);
        for (Map<String, String> item : data){
            String msgType = item.get("pageType");
            String pageTitle = item.get("pageTitle");
            String pageLink = item.get("pageLink");
            String troperName = item.get("troperName");
            String timeStamp = dateConverter(item.get("timeStamp"));
            actions.generateMessage(this, msgType, pageTitle, pageLink, troperName, timeStamp);
        }
    }

    public void refreshManual(View view){
        fetchMessages();
    }

    public void generateTestMessage(View view) {
        refresh_rate = 0;
        clearMessages();
        String msgType = "Test";
        String pageTitle = "Administrivia / Welcome to TV Tropes";
        String pageLink = "https://tvtropes.org/pmwiki/pmwiki.php/Administrivia/WelcomeToTVTropes";
        String troperName = "This and That Troper";
        String timeStamp = dateConverter("11th Jun 11:55 PM");
        actions.generateMessage(this, msgType, pageTitle, pageLink, troperName, timeStamp);
    }
}