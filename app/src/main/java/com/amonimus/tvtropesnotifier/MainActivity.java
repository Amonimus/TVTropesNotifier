package com.amonimus.tvtropesnotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GlobalFunctions globalfuncs;
    private BackgroundProcess background;
    private MessageActions actions;
    private Thread repeatTaskThread;
    TextView debugTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("TVTN Notification", "TVTN Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        globalfuncs = new GlobalFunctions(this);
        background = new BackgroundProcess(this);
        actions = new MessageActions(this);
        debugTextView = this.findViewById(R.id.debugTextView);
        background.testLogin();
        loopTask();
    }

    public void loopTask() {
        repeatTaskThread = new Thread() {
            public void run() {
                while (true) {
                    if (globalfuncs.getRefreshRate() < 1 && globalfuncs.getSigninStatus()) {
                        try {
                            Thread.sleep(globalfuncs.getRefreshRate() * 1000);
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
                            Thread.sleep(globalfuncs.getRefreshRate() * 60000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        repeatTaskThread.start();
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
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logoButtonClick(View view){
        globalfuncs.openLink("https://tvtropes.org/");
    }

    public void datesorting(List list){
        boolean sortlatest = globalfuncs.getSorting();
        DateFormat f = new SimpleDateFormat("MMM dd yyyy, hh:mm aa");
        Collections.sort(list, new Comparator<Map<String, String>>(){
            @Override
            public int compare(Map<String, String> object1, Map<String, String> object2) {
                try {
                    if (sortlatest) {
                        return f.parse(dateConverter(object2.get("timeStamp"))).compareTo(f.parse(dateConverter(object1.get("timeStamp"))));
                    } else {
                        return f.parse(dateConverter(object1.get("timeStamp"))).compareTo(f.parse(dateConverter(object2.get("timeStamp"))));
                    }
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
        if (globalfuncs.getSigninStatus()) {
            List<Map> data = background.fetch();
            if (data != null) {
                datesorting(data);
                for (Map<String, String> item : data) {
                    String msgType = item.get("pageType");
                    String pageTitle = item.get("pageTitle");
                    String pageLink = item.get("pageLink");
                    String troperName = item.get("troperName");
                    String timeStamp = dateConverter(item.get("timeStamp"));
                    actions.generateMessage(msgType, pageTitle, pageLink, troperName, timeStamp);
                }
            } else {
                debugTextView.setText("Failed to recieve data.");
            }
        } else {
            debugTextView.setText("Not logged in.");
        }
    }

    public void refreshManual(View view){
        fetchMessages();
    }

    public void generateTestMessage(View view) {
        clearMessages();
        String msgType = "Test";
        String pageTitle = "Administrivia / Welcome to TV Tropes";
        String pageLink = "https://tvtropes.org/pmwiki/pmwiki.php/Administrivia/WelcomeToTVTropes";
        String troperName = "This and That Troper";
        String timeStamp = dateConverter("11th Jun 11:55 PM");
        actions.generateMessage(msgType, pageTitle, pageLink, troperName, timeStamp);
    }
}