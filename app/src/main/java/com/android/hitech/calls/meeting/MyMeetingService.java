package com.android.hitech.calls.meeting;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyMeetingService extends Service {
    LocationManager lm;
    public static final String mypreference = "mypref";
    public static final String user = "userKey";
    Timer timer = new Timer();
    Geocoder geocoder;
    boolean bool = false;
    SharedPreferences sf, msf;
    long s_MeetingEndTimeL, s_MeetingStarttimeL, s_MeetingDuration, s_TravelTime, s_TotalMeetingtime;
    String s_MeetingEndTime, s_MeetingStarttime, targetAdd;
    long travelStartTime, travelEndTime, travelTime, meetEndTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        targetAdd = intent.getStringExtra("targetAddress");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
            }
        };
        timer.schedule(task, 0,20000);
        return START_REDELIVER_INTENT;
    }

    private void doAll(Location location) {
        SQLiteDatabase database = new DbAutoSave(getBaseContext()).getWritableDatabase();
        msf = getSharedPreferences(mypreference, MODE_PRIVATE);
        geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> addressList = geocoder.getFromLocationName(targetAdd, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                Location location1 = new Location(LocationManager.NETWORK_PROVIDER);
                location1.setLatitude(address.getLatitude());
                location1.setLongitude(address.getLongitude());
                float distance = location.distanceTo(location1);
                sf = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                if (distance < 50) {
                    if (!bool) {
                        travelStartTime = sf.getLong("TravelStartTime", 0);
                        travelEndTime = System.currentTimeMillis();
                        travelTime = travelEndTime - travelStartTime;
                        ContentValues cv = new ContentValues();
                        cv.put("StartDate", sf.getLong("datakey", -1));
                        cv.put("MeetingId", sf.getString("MeetingId",null));
                        cv.put("MeetingStartTime", travelEndTime);
                        cv.put("TravelDuration", travelTime);
                        database.insert("meetings", null, cv);
                    }
                    bool = true;
                } else if (distance < 300) {
                    if (bool) {
                        meetEndTime = System.currentTimeMillis();
                        ContentValues cv = new ContentValues();
                        cv.put("MeetingEndTime", meetEndTime);
                        database.update("meetings", cv, "StartDate=" + sf.getLong("datakey", -1), null);
                        Cursor cursor = database.rawQuery("select * from meetings where StartDate=" + sf.getLong("datakey", -1), null);
                        if (cursor.moveToLast()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault());
                            s_MeetingStarttimeL = cursor.getLong(3);
                            s_MeetingStarttime = sdf.format(s_MeetingStarttimeL);
                            s_TravelTime = cursor.getLong(4);
                            s_MeetingEndTimeL = cursor.getLong(5);
                            s_MeetingEndTime = sdf.format(s_MeetingEndTimeL);
                            s_MeetingDuration = s_MeetingEndTimeL - s_MeetingStarttimeL;
                            s_TotalMeetingtime = s_TravelTime + s_MeetingDuration;
                            sendTrackingToServer();
                        }
                        cursor.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTrackingToServer() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://resume.globalhunt.in/api/HiTechApp/IntsertMeetingTrack", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i("MyMeetingServiceMeet", "ServerResponse :" + response);
                bool = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("MyMeetingServiceMeet", "ServerResponse :" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                map.put("hitechApiKey", "hitechApiX@123#");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                map.put("UserId", msf.getString(user, null));
                map.put("MeetingStarttime", s_MeetingStarttime);
                map.put("MeetingEndTime", s_MeetingEndTime);
                map.put("MeetingDuration", String.valueOf(s_MeetingDuration));
                map.put("TravelTime", String.valueOf(s_TravelTime));
                map.put("TotalMeetingtime", String.valueOf(s_TotalMeetingtime));
                map.put("MeetingId", sf.getString("MeetingId", null));
                Log.i("sendingmap", String.valueOf(map));
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(getBaseContext()).addToRequestQueue(request);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            doAll(location);
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

}
