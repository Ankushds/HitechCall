package com.android.hitech.calls.call_logs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.hitech.calls.R;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.homepage.SessionManager;
import com.android.hitech.calls.meeting.Phonemeeting;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class CustomList extends ArrayAdapter<String> {
    Context context;
    List<String> phNames, time, calldura, calldirection;
    String numm, idOfUser;
    SessionManager session;
    LinearLayout adddd;
    public static final String mypreference = "mypref";
    public static final String user = "userKey";
    SharedPreferences sharedpreferences;
    TextView txtPhone, txtDate, aktext;
    ImageView iv2, iv3, iv4;
    View rowView;
    DbAutoSave dbAutoSave,dbAutoSave1;
    boolean bool = true;

    public CustomList(Context context, List<String> names, List<String> time, List<String> calldura, List<String> calldirection) {
        super(context, R.layout.custom_mobile_layout, names);
        this.context = context;
        this.phNames = names;
        this.time = time;
        this.calldura = calldura;
        this.calldirection = calldirection;
        sharedpreferences = context.getSharedPreferences(mypreference, context.MODE_PRIVATE);
        idOfUser = sharedpreferences.getString(user, "");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbAutoSave = new DbAutoSave(context);
        if (sharedpreferences.getInt("integer", 0) == 1) {
            if (!dbAutoSave.getData(time.get(position))) {
                rowView = inflater.inflate(R.layout.custom_mobile_layout1, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.custom_mobile_layout, null, false);
            }
            if (bool) {
                System.out.println("RawDataIF : "+bool);
                new MyThread().start();
                bool = false;
            }
        } else {
            rowView = inflater.inflate(R.layout.custom_mobile_layout, null, false);
        }
        adddd = (LinearLayout) rowView.findViewById(R.id.addd);
        session = new SessionManager();
        txtPhone = (TextView) rowView.findViewById(R.id.textPhone);
        txtDate = (TextView) rowView.findViewById(R.id.datetext);
        iv2 = (ImageView) rowView.findViewById(R.id.img2);
        iv3 = (ImageView) rowView.findViewById(R.id.img3);
        iv4 = (ImageView) rowView.findViewById(R.id.img4);
        txtPhone.setText(phNames.get(position));
        txtDate.setText(time.get(position));
        iv2.setOnClickListener(listener);
        iv3.setOnClickListener(listener);
        iv4.setOnClickListener(listener);
        if (dbAutoSave.getData(time.get(position))) {
            aktext = (TextView) rowView.findViewById(R.id.aktext);
            aktext.setTextColor(Color.parseColor("#ff669900"));
            aktext.setText("saved");
        }
        return rowView;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View v1 = (View) v.getParent();
            View v2 = (View) v1.getParent();
            ListView v3 = (ListView) v2.getParent();
            int positionn = v3.getPositionForView(v2);
            numm = phNames.get(positionn);
            switch (v.getId()) {
                case R.id.img2:
                    Intent i = new Intent(getContext(), Phonemeeting.class);
                    i.putExtra("phoneno", numm);
                    context.startActivity(i);
                    break;
                case R.id.img3:
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + numm));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    break;
                case R.id.img4:
                    Intent in = new Intent(Intent.ACTION_SENDTO);
                    in.setData(Uri.parse("smsto:" + numm));
                    in.putExtra("sms_body", "Hi from HiTech Calls");
                    if (in.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(in);
                    }
                    break;
            }
        }
    };

    private class MyThread extends Thread {
        String serverURL = "https://resume.globalhunt.in/api/HiTechApp/CreateCallLogList";
        String oppurdata, callTime, callDir, callPhone, callDuration, outResult, outDate;
        int i;
        byte[] postData;

        @Override
        public void run() {
            JSONArray jArray = new JSONArray();
            dbAutoSave1 = new DbAutoSave(context);
            for (i = 0; i < calldirection.size(); i++) {
                if (!dbAutoSave1.getData(time.get(i))) {
                    System.out.println("RawData : "+dbAutoSave1.getData(time.get(i)));
                    callTime = time.get(i);
                    callDuration = calldura.get(i);
                    callDir = calldirection.get(i);
                    callPhone = phNames.get(i);
                    callPhone = callPhone.trim();
                    callPhone = callPhone.replace("+91", "");
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject.put("UserId", idOfUser);
                        jObject.put("DateStart", callTime);
                        jObject.put("CallDuration", callDuration);
                        jObject.put("Direction", callDir);
                        jObject.put("Mobile", callPhone);
                        jArray.put(jObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (jArray.length() != 0) {
                oppurdata = jArray.toString();
                BufferedReader reader;
                postData = oppurdata.getBytes(StandardCharsets.UTF_8);
                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("hitechApiKey", "hitechApiX@123#");
                    conn.setRequestProperty("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                    conn.setConnectTimeout(20000);
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(postData);
                    wr.flush();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        outResult = obj.getString("RecordId");
                        outDate = obj.getString("DateStart");
                        outDate = outDate.replace('T', ' ');
                        if (outDate != null) {
                            Date dt = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).parse(outDate);
                            outDate = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).format(dt);
                            dbAutoSave.insertData(outDate, outResult);
                        }
                        sharedpreferences.edit().putBoolean("res_all", false).apply();
                    }
                    dbAutoSave.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
