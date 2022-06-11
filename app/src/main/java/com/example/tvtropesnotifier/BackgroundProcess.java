package com.example.tvtropesnotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BackgroundProcess {
    String USERNAME = "Amonimus";
    String PASSWORD = "ZTQCkg86AMydCP9";

    public List fetch() {
        NetThread netthread = new NetThread(USERNAME, PASSWORD);
        netthread.start();
        try {
            netthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return netthread.getValue();
     }
}
