package com.busticket.amedora.busticketsrl.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Amedora on 9/1/2015.
 */
public class AppStatus {
    private static  AppStatus instance = new AppStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    public static AppStatus getInstance(Context ctx){
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline(){

            boolean success = false;
            try {
                URL url = new URL("https://google.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();
                success = connection.getResponseCode() == 200;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return success;

    }

}
