package com.amonimus.tvtropesnotifier;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amonimus.tvtropesnotifier.R;

public class MessageActions {
    private MainActivity activity;
    private GlobalFunctions globalfuncs;
    private int notifier_ID;
    MessageActions(MainActivity activity) {
        this.activity = activity;
        globalfuncs = new GlobalFunctions(activity);
        notifier_ID = 0;
    }

    private void createNotification(String msgType, String notifText, String notifLink) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "TVTN Notification");
        builder.setGroup("TVTN Notification Group");
        if (msgType == "Page") {
            builder.setContentTitle("New edit");
        } else if (msgType == "Forum"){
            builder.setContentTitle("New post");
        }
        builder.setContentText(notifText);
        builder.setSmallIcon(R.drawable.ic_tvth_icon);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notifText));
        builder.setAutoCancel(true);

        Uri uri = Uri.parse(notifLink);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);
        managerCompat.notify(notifier_ID, builder.build());
        notifier_ID++;
    }

    private void buildMessage(LinearLayout container, String pageType, String pageTitle, String pageLink, String troperName, String timeStamp) {
        int getContainerWidth = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout messageBlock = new LinearLayout(activity);
        messageBlock.setOrientation(LinearLayout.VERTICAL);
        messageBlock.setLayoutParams(new LinearLayout.LayoutParams(getContainerWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageBlock.setBackgroundColor(activity.getResources().getColor(R.color.tvt_grey));

        LinearLayout messageBlock1 = new LinearLayout(activity);
        messageBlock1.setOrientation(LinearLayout.HORIZONTAL);
        messageBlock1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout messageBlock2 = new LinearLayout(activity);
        messageBlock2.setOrientation(LinearLayout.HORIZONTAL);
        messageBlock2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView messageType = new TextView(activity);
        messageType.setText("["+pageType+"] ");
        messageType.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageBlock1.addView(messageType);

        TextView messagePageTitle = new TextView(activity);
        messagePageTitle.setText(pageTitle);
        messagePageTitle.setLayoutParams(new LinearLayout.LayoutParams(getContainerWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageBlock1.addView(messagePageTitle);

        messageBlock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                globalfuncs.openLink(pageLink);
                activity.refreshManual(view);
            }
        });

        TextView messageTroper = new TextView(activity);
        messageTroper.setText("by "+troperName+" ");
        messageTroper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageBlock2.addView(messageTroper);

        TextView messageDate = new TextView(activity);
        messageDate.setText("// "+timeStamp);
        messageDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageBlock2.addView(messageDate);

        View divider = new View(activity);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 32));
        divider.setBackgroundColor(activity.getResources().getColor(R.color.black));

        messageBlock.addView(divider);
        messageBlock.addView(messageBlock1);
        messageBlock.addView(messageBlock2);

        container.addView(messageBlock);
    }

    public void generateMessage(String msgType, String pageTitle, String pageLink, String troperName, String timeStamp) {
        LinearLayout container = activity.findViewById(R.id.messageContainer);
        buildMessage(container, msgType, pageTitle, pageLink, troperName, timeStamp);
        if (globalfuncs.getNotif()) {
            createNotification(msgType, pageTitle + " by " + troperName, pageLink);
        }
    }
}
