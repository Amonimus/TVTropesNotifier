package com.example.tvtropesnotifier;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NetThread extends Thread {
    String returnstring = "";
    String USERNAME;
    String PASSWORD;
    Map<String, String> loginCookies;
    List data = new ArrayList<Map>();

    public NetThread(String USERNAME, String PASSWORD) {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public void run() {
        login(USERNAME, PASSWORD);
        data = fetchPages();
        data.addAll(fetchForums());
    }

    public void login(String USERNAME, String PASSWORD) {
        try {
            Connection.Response loginResponse = Jsoup.connect("https://tvtropes.org/pmwiki/validate.php")
                    .data("ajax", "1")
                    .data("handle", USERNAME)
                    .data("pass", PASSWORD)
                    .data("remember", "on")
                    .method(Connection.Method.POST)
                    .execute();
            loginCookies = loginResponse.cookies();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List fetchPages() {
        Document doc = null;
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
                returnstring = "Not logged in";
            } else {
                Elements tr_list = doc.getElementsByTag("tr");
                for (Element tr : tr_list) {
                    if (tr.attributes().toString().contains("dark")) {
                        Elements cols = tr.getElementsByTag("td");
                        Element firstCol = cols.first();
                        timeStamp = firstCol.text().replace("Unfollow ", "").replace("at ", "");
                        pageTitle = firstCol.nextElementSibling().text().replaceAll("\\[\\d+\\] ", "");
                        troperName = firstCol.nextElementSibling().nextElementSibling().text();
                        if (pageTitle.equals("Main: Ask The Tropers")) {
                            pageTitle = "Ask The Tropers";
                            pageLink = "https://tvtropes.org/pmwiki/query.php?type=att";
                        } else {
                            pageLink = "https://tvtropes.org/" + firstCol.nextElementSibling().nextElementSibling().nextElementSibling().getElementsByTag("a").attr("href");
                        }
                        Map<String, String> messagePack = new HashMap<String, String>();
                        messagePack.put("pageType", "Page");
                        messagePack.put("timeStamp", timeStamp);
                        messagePack.put("pageTitle", pageTitle);
                        messagePack.put("troperName", troperName);
                        messagePack.put("pageLink", pageLink);
                        fullPackage.add(messagePack);
                        Log.d("FETCH", "Package complete");
                    }
                }
                returnstring = "Logged in";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPackage;
    }

    public List fetchForums() {
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
                returnstring = "Not logged in";
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
                returnstring = "Logged in";
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
