package com.amonimus.tvtropesnotifier;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LoginThread extends Thread {
    private String USERNAME;
    private String PASSWORD;
    private Map<String, String> loginCookies;
    private Boolean result;

    public LoginThread(String USERNAME, String PASSWORD) {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public void run(){
        result = login();
    }

    public Boolean login() {
        Log.d("-- DBG", "Trying to login");
        try {
            Log.d("-- DBG", PASSWORD);
            Connection.Response loginResponse = Jsoup.connect("https://tvtropes.org/pmwiki/validate.php")
                    .data("ajax", "1")
                    .data("handle", USERNAME)
                    .data("pass", PASSWORD)
                    .data("remember", "on")
                    .method(Connection.Method.POST)
                    .execute();
            loginCookies = loginResponse.cookies();

            String json = loginResponse.body();
            Log.d("--JSON", json);
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            Boolean success = "1".equals(object.getString("success"));
            String msg = object.getString("msg").split(",")[0];
            return success;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean returnValue(){
        return result;
    }

    public Map returnCookies(){
        return loginCookies;
    }
}

class NetThread extends Thread {
    private String USERNAME;
    private String PASSWORD;
    private Map<String, String> loginCookies;
    private List data = new ArrayList<Map>();

    public NetThread(String USERNAME, String PASSWORD, Map<String, String> cookies) {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        loginCookies = cookies;
    }

    public void run() {
        data = fetchPages();
        data.addAll(fetchForums());
    }

    private List fetchPages() {
        Document doc = null;
        String pageType = "Page";
        String timeStamp;
        String pageTitle;
        String troperName;
        String pageLink;
        List fullPackage = new ArrayList();
        try {
            doc = Jsoup.connect("https://tvtropes.org/pmwiki/awl.php")
                    .cookies(loginCookies)
                    .get();
            if (doc.title().contains("LoginPrompt")) {
                //
            } else {
                Elements tr_list = doc.getElementsByTag("tr");
                for (Element tr : tr_list) {
                    if (tr.attributes().toString().contains("dark")) {
                        Elements cols = tr.getElementsByTag("td");
                        Element firstCol = cols.first();
                        timeStamp = firstCol.text().replace("Unfollow ", "").replace("at ", "");
                        pageTitle = firstCol.nextElementSibling().text().replaceAll("\\[\\d+\\] ", "");
                        troperName = firstCol.nextElementSibling().nextElementSibling().text();
                        switch (pageTitle) {
                            case "Main: Ask The Tropers": {
                                pageTitle = "Ask The Tropers";
                                pageLink = "https://tvtropes.org/pmwiki/query.php?type=att";
                                pageType = "Query";
                                break;
                            }
                            case "Main: Query Wishlist": {
                                pageTitle = "Query Wishlist";
                                pageLink = "https://tvtropes.org/pmwiki/query.php?type=wl";
                                pageType = "Query";
                                break;
                            }
                            case "Main: Query Bugs": {
                                pageTitle = "Query Bugs";
                                pageLink = "https://tvtropes.org/pmwiki/query.php?type=bug";
                                pageType = "Query";
                                break;
                            }
                            case "Main: Trope Finder": {
                                pageTitle = "Trope Finder";
                                pageLink = "https://tvtropes.org/pmwiki/query.php?type=tf";
                                pageType = "Query";
                                break;
                            }
                            case "Main You Know That Show...": {
                                pageTitle = "You Know That Show...";
                                pageLink = "https://tvtropes.org/pmwiki/query.php?type=ykts";
                                pageType = "Query";
                                break;
                            }
                            default: {
                                pageLink = "https://tvtropes.org/" + firstCol.nextElementSibling().nextElementSibling().nextElementSibling().getElementsByTag("a").attr("href");
                                pageType = "Page";
                            }
                        }
                        Map<String, String> messagePack = new HashMap<String, String>();
                        messagePack.put("pageType", pageType);
                        messagePack.put("timeStamp", timeStamp);
                        messagePack.put("pageTitle", pageTitle);
                        messagePack.put("troperName", troperName);
                        messagePack.put("pageLink", pageLink);
                        fullPackage.add(messagePack);
                        Log.d("FETCH", "Package complete");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPackage;
    }

    private List fetchForums() {
        Document doc = null;
        String timeStamp;
        String pageTitle;
        String troperName;
        String pageLink;
        List fullPackage = new ArrayList();
        try {
            doc = Jsoup.connect("https://tvtropes.org/pmwiki/thread_watch.php")
                    .cookies(loginCookies)
                    .get();
            if (doc.title().contains("LoginPrompt")) {
                //
            } else {
                Elements tr_list = doc.getElementsByTag("tr");
                for (Element tr : tr_list) {
                    Elements cols = tr.getElementsByTag("td");
                    if (! cols.isEmpty()) {
                        Element firstCol = cols.first();
                        Elements find_checkmark = firstCol.nextElementSibling().getElementsByTag("i");
                        if (! find_checkmark.isEmpty()){
                            if (find_checkmark.toString().contains(("fa-check-circle"))){
                                pageTitle = firstCol.nextElementSibling().text();
                                timeStamp = firstCol.nextElementSibling().nextElementSibling().nextElementSibling().text();
                                troperName = firstCol.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text();
                                pageLink = "https://tvtropes.org/" + firstCol.nextElementSibling().getElementsByTag("a").attr("href");

                                Map<String, String> messagePack = new HashMap<String, String>();
                                messagePack.put("pageType", "Forum");
                                messagePack.put("timeStamp", timeStamp);
                                messagePack.put("pageTitle", pageTitle);
                                messagePack.put("troperName", troperName);
                                messagePack.put("pageLink", pageLink);
                                fullPackage.add(messagePack);
                                Log.d("FETCH", "Package complete");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPackage;
    }

    public List<Map> getValue() {
        return data;
    }
}
