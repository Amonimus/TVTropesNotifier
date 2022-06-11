package com.example.tvtropesnotifier;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_window);
    }

    public void openLink(View view) {
        Button btn = (Button) view;

        String btn_text = btn.getText().toString();
        String target_url = "https://tvtropes.org/";
        switch(btn_text) {
            case "Troper Page" : target_url = "https://tvtropes.org/pmwiki/profile.php"; break;
            case "To Do List" : target_url = "https://tvtropes.org/pmwiki/to_do_list.php"; break;
            case "Private Messages" : target_url = "https://tvtropes.org/pmwiki/pm.php"; break;
            case "Followed Pages" : target_url = "https://tvtropes.org/pmwiki/awl.php"; break;
            case "Followed Threads" : target_url = "https://tvtropes.org/pmwiki/thread_watch.php"; break;
            case "Queries" : target_url = "https://tvtropes.org/pmwiki/user_queries.php"; break;
            case "Profile Settings" : target_url = "https://tvtropes.org/pmwiki/profile.php"; break;

            case "Ask The Tropers" : target_url = "https://tvtropes.org/pmwiki/query.php?type=att"; break;
            case "Trope Finder" : target_url = "https://tvtropes.org/pmwiki/query.php?type=tf"; break;
            case "You Know That Show" : target_url = "https://tvtropes.org/pmwiki/query.php?type=ykts"; break;
            case "Wishlist" : target_url = "https://tvtropes.org/pmwiki/query.php?type=wl"; break;
            case "Bugs" : target_url = "https://tvtropes.org/pmwiki/query.php?type=bug"; break;

            case "Trope Launch Pad" : target_url = "https://tvtropes.org/pmwiki/tlp_activity.php"; break;
            case "Trope Repair Shop" : target_url = "https://tvtropes.org/pmwiki/conversations.php?topic=renames"; break;
            case "Image Picking" : target_url = "https://tvtropes.org/pmwiki/conversations.php?topic=images"; break;

            case "Cut List" : target_url = "https://tvtropes.org/pmwiki/cutlist.php"; break;
            case "Administrivia" : target_url = "https://tvtropes.org/pmwiki/pmwiki.php/Main/Administrivia"; break;
            case "Text Formatting Rules" : target_url = "https://tvtropes.org/pmwiki/pmwiki.php/Administrivia/TextFormattingRules"; break;
        }
        Uri uri = Uri.parse(target_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}