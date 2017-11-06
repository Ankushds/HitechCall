package com.android.hitech.calls.Unused;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.homepage.Home_nav;
import com.android.hitech.calls.meeting.MyMeetingService;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CallReceiver extends BroadcastReceiver {
    String number, tab_row, tabDate, dir, datess, ids, callDur, phone, id_user;
    long callDate;
    String url = "https://resume.globalhunt.in/api/HiTechApp/CreateCallLog";
    public static final String mypreference = "mypref";
    JSONObject jobj;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (stateStr != null) {
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                System.out.println("AnkushKaMBOJJJJ :"+TelephonyManager.EXTRA_STATE_IDLE);
             context.stopService(new Intent(new Intent(context,MyRecordingService.class)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendToHost();
                    }
                }, 3000);
            }else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                System.out.println("AnkushKaMBOJJJJ :"+TelephonyManager.EXTRA_STATE_OFFHOOK);
                context.startService(new Intent(context,MyRecordingService.class));
            }
        }
    }


    public void sendToHost() {
        getSendData();
        StringRequest objectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        jobj = new JSONObject(response);
                        tab_row = jobj.getString("RecordId");
                        tabDate = jobj.getString("DateStart");
                        tabDate = tabDate.replace('T', ' ');
                        Date dt = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).parse(tabDate);
                        tabDate = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).format(dt);
                        if (tabDate != null) {
                            Toast.makeText(context, "Log Saved Sucessfully", Toast.LENGTH_LONG).show();
                            DbAutoSave dbAutoSave = new DbAutoSave(context);
                            dbAutoSave.insertData(tabDate, tab_row);
                            senddatasms();
                        } else {
                            Toast.makeText(context, "Error : Please Save Manually", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex) {
                        File file = new File(Environment.getExternalStorageDirectory()
                                .getAbsolutePath(), "Ht_Exception");
                        if (file.exists()) {
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/Ht_Exception/", "exception.txt");
                            if (file1.exists()) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(file1, true);
                                    OutputStreamWriter writer = new OutputStreamWriter(fos);
                                    writer.append(ex.getMessage());
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                File file2 = new File(file, "exception.txt");
                                try {
                                    FileOutputStream fos = new FileOutputStream(file2, true);
                                    OutputStreamWriter writer = new OutputStreamWriter(fos);
                                    writer.append(ex.getMessage());
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (file.mkdir()) {
                                File file1 = new File(Environment.getExternalStorageDirectory() + "/Ht_Exception/", "exception.txt");
                                if (file1.exists()) {
                                    try {
                                        FileOutputStream fos = new FileOutputStream(file1, true);
                                        OutputStreamWriter writer = new OutputStreamWriter(fos);
                                        writer.append(ex.getMessage());
                                        writer.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    File file2 = new File(file, "exception.txt");
                                    try {
                                        FileOutputStream fos = new FileOutputStream(file2, true);
                                        OutputStreamWriter writer = new OutputStreamWriter(fos);
                                        writer.append(ex.getMessage());
                                        writer.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(new Intent(context, Home_nav.class).putExtra("call", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    Toast.makeText(context, "Error : Please Save Manually", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, Home_nav.class).putExtra("call", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "Ht_Exception");
                if (file.exists()) {
                    File file1 = new File(Environment.getExternalStorageDirectory() + "/Ht_Exception/", "exception.txt");
                    if (file1.exists()) {
                        try {
                            FileWriter fw = new FileWriter(file1);
                            fw.append(ex.getMessage());
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        File file2 = new File(file, "exception.txt");
                        try {
                            FileWriter fw = new FileWriter(file2);
                            fw.append(ex.getMessage());
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (file.mkdir()) {
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Ht_Exception/", "exception.txt");
                        if (file1.exists()) {
                            try {
                                FileWriter fw = new FileWriter(file1);
                                fw.append(ex.getMessage());
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            File file2 = new File(file, "exception.txt");
                            try {
                                FileWriter fw = new FileWriter(file2);
                                fw.append(ex.getMessage());
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Toast.makeText(context, "Error : Please Save Manually", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, Home_nav.class).putExtra("call", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("hitechApiKey", "hitechApiX@123#");
                hashMap.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return hashMap;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mMap = new HashMap<>();
                mMap.put("Content-Type", "application/x-www-form-urlencoded");
                mMap.put("UserId", id_user);
                mMap.put("DateStart", datess);
                mMap.put("CallDuration", callDur);
                mMap.put("Direction", dir);
                mMap.put("Mobile", phone);
                return mMap;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(context).addToRequestQueue(objectRequest);
    }

    public void senddatasms() {
        String address = "https://resume.globalhunt.in/api/HiTechApp/SendMessage";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("hitechApiKey", "hitechApiX@123#");
                hashMap.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return hashMap;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> hMap = new HashMap<>();
                hMap.put("Content-Type", "application/x-www-form-urlencoded");
                hMap.put("UserId", id_user);
                hMap.put("CallDuration", callDur);
                hMap.put("Direction", dir);
                hMap.put("Mobile", phone);
                return hMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getSendData() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        id_user = sharedpreferences.getString("userKey", "");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC LIMIT 1");
        if (cursor != null) {
            cursor.moveToLast();
            int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = cursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int id = cursor.getColumnIndex(CallLog.Calls._ID);
            phone = cursor.getString(number);
            phone = phone.trim();
            phone = phone.replace("+91", "");
            String callType = cursor.getString(type);
            callDate = cursor.getLong(date);
            ids = cursor.getString(id);
            datess = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).format(callDate);
            callDur = cursor.getString(duration);
            dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            cursor.close();
        }
    }
}
