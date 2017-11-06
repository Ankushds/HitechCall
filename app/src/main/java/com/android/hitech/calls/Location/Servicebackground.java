package com.android.hitech.calls.Location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Servicebackground extends Service {
    public double latitude;
    public double longitude;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String user = "userKey";
    String idofuser, add0, add1;
    LocationManager lm;
    Timer timer = new Timer();
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            startTimerTask();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        idofuser = sharedpreferences.getString(user, "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
            }
        };
        timer.schedule(task, 0,5*60000);
        return START_STICKY;
    }

    private void startTimerTask() {
        new Senddata().execute();
        new SendGpsStatus().execute();
    }

    public class Senddata extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {
            Log.i("serverlocation",latitude+" , "+longitude);
            add0 = "https://resume.globalhunt.in/api/HiTechApp/InsertUpdateTrackLocation?latitude=" + latitude + "&longitude=" + longitude + "&userId=" + idofuser;
        }

        protected Void doInBackground(String...urls) {
            BufferedReader reader;
            try {
                URL url = new URL(add0);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("hitechApiKey", "hitechApiX@123#");
                conn.setRequestProperty("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Exception ex) {
            }
            return null;
        }
    }

    private class SendGpsStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add1 = "https://resume.globalhunt.in/api/HiTechApp/UpdateGPS?UserId=" + idofuser + "&status=1";
            } else {
                add1 = "https://resume.globalhunt.in/api/HiTechApp/UpdateGPS?UserId=" + idofuser + "&status=0";
            }
        }

        @Override
        protected String doInBackground(String... params) {
            BufferedReader rd;
            try {
                URL url = new URL(add1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("hitechApiKey", "hitechApiX@123#");
                connection.setRequestProperty("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
            }
            return null;
        }

    }

}



