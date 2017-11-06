package com.android.hitech.calls.Alert_popup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.hitech.calls.R;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.homepage.Home_nav;
import com.android.hitech.calls.homepage.SessionManager;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCall1 extends AppCompatActivity {
    static EditText mobile, anchorinfo, calldesc;
    SessionManager session;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String user = "userKey";
    private String phoneNos;
    private String callTypes;
    String idofuser, getaccid, accid, tab_row, callDurations, callDates, c_n;
    String getrr, idoppur, callDurationn, dirrr, dateTime, ggg, idjob, c_idd, getcalldesc, mobileno, getaction, getsubject;
    Spinner profileSpinner, subjectspinner, actionspinner;
    ArrayAdapter<String> actionadap, subjectadap, adaptercity, adapterModule;
    ArrayList<String> list1 = new ArrayList<>();
    ArrayList<String> oppurlist = new ArrayList<>();
    ArrayList<String> listaccount = new ArrayList<>();
    ArrayList<String> listcontact = new ArrayList<>();
    List<String> strList;
    List<String> strrr;
    JSONObject jobj, jobj1;
    ArrayAdapter<String> datAdapter1;
    Context context;
    ProgressDialog pb;
    String[] clientCall, introductoryCall, meetingCall, pOCClientCall, agreementCall, positionCall, offerJoining, candidateCAll, jobCall, feedbackCall, interviewCall, offeJoinCall;
    String[] docuFolUpCal = {"Asked for the list of Document", "Confirmation on list of document recieved"};
    String[] profileStr = {"Accounts", "Candidates", "Contacts", "Opportunities",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_call);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        context = this.getBaseContext();
        getRecentCallDetails();
        session = new SessionManager();
        mobile = (EditText) findViewById(R.id.edtMobile);
        calldesc = (EditText) findViewById(R.id.calldescription);
        anchorinfo = (EditText) findViewById(R.id.anchor);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            anchorinfo.setText(sharedpreferences.getString(Name, ""));
        }
        String phone = getIntent().getStringExtra("phoneno");
        dateTime = getIntent().getStringExtra("DataTime");
        callDurationn = getIntent().getStringExtra("CallDuration");
        dirrr = getIntent().getStringExtra("calldiree");
        profileSpinner = (Spinner) findViewById(R.id.spinProfileItem);
        subjectspinner = (Spinner) findViewById(R.id.spinnerItem3);
        actionspinner = (Spinner) findViewById(R.id.spinProAction);
        adapterModule = new ArrayAdapter<>(context, R.layout.spinner_item, profileStr);
        profileSpinner.setAdapter(adapterModule);
        if (getCallTypes() == getCallTypes()) {
            if (getCallTypes().contains("INCOMING")) {
                strList = new ArrayList<>(Arrays.asList("INCOMING"));
            }
            if (getCallTypes().contains("OUTGOING")) {
                strList = new ArrayList<>(Arrays.asList("OUTGOING"));
            }
            try {

                if (getCallTypes().contains("MISSED")) {
                    strList = new ArrayList<>(Arrays.asList("MISSED"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        datAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strrr);
        datAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobile.setText(phone);
        mobileno = phone;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pb = new ProgressDialog(this);
        pb.setMessage("Please wait fetching info...");
        pb.setCancelable(false);
        pb.create();
        pb.show();
        longOperation();
        candidateCAll = getResources().getStringArray(R.array.candidateCAll);
        clientCall = context.getResources().getStringArray(R.array.clientCall);
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getrr = accid;
                    getaccid = accid;
                    adaptercity = new ArrayAdapter<>(context, R.layout.spinner_item, listaccount);
                    subjectadap = new ArrayAdapter<>(context, R.layout.spinner_item, clientCall);
                } else if (position == 1) {
                    getrr = idjob;
                    adaptercity.notifyDataSetChanged();
                    adaptercity = new ArrayAdapter<>(context, R.layout.spinner_item, list1);
                    subjectadap = new ArrayAdapter<>(context, R.layout.spinner_item, candidateCAll);
                } else if (position == 2) {
                    getrr = c_idd;
                    System.out.println("position is 2" + getrr);
                    adaptercity = new ArrayAdapter<>(context, R.layout.spinner_item, listcontact);
                    subjectadap = new ArrayAdapter<>(context, R.layout.spinner_item, clientCall);
                } else if (position == 3) {
                    getrr = idoppur;
                    System.out.println("position is 3" + getrr);
                    adaptercity = new ArrayAdapter<>(context, R.layout.spinner_item, oppurlist);
                    subjectadap = new ArrayAdapter<>(context, R.layout.spinner_item, candidateCAll);
                }
                subjectspinner.setAdapter(subjectadap);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        subjectspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                offerJoining = getResources().getStringArray(R.array.offerJoining);
                introductoryCall = context.getResources().getStringArray(R.array.introductoryCall);
                meetingCall = context.getResources().getStringArray(R.array.meetingCall);
                pOCClientCall = getResources().getStringArray(R.array.pOCClientCall);
                agreementCall = getResources().getStringArray(R.array.agreementCall);
                positionCall = getResources().getStringArray(R.array.positionCall);
                offerJoining = getResources().getStringArray(R.array.offerJoining);
                jobCall = getResources().getStringArray(R.array.jobCall);
                feedbackCall = getResources().getStringArray(R.array.feedbackCall);
                interviewCall = getResources().getStringArray(R.array.interviewCall);
                offeJoinCall = getResources().getStringArray(R.array.offeJoinCall);
                switch (position) {
                    case 0:
                        if (parent.getItemAtPosition(position).toString().contentEquals("Introductory Call")) {

                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, introductoryCall);
                        } else {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, jobCall);
                        }
                        break;
                    case 1:
                        if (parent.getItemAtPosition(position).toString().contentEquals("Meeting Call")) {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, meetingCall);
                        } else {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, feedbackCall);
                        }
                        break;
                    case 2:
                        if (parent.getItemAtPosition(position).toString().contentEquals("New POC-Client Call")) {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, pOCClientCall);
                        } else {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, interviewCall);
                        }
                        break;
                    case 3:
                        if (parent.getItemAtPosition(position).toString().contentEquals("Agreement Call")) {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, agreementCall);
                        } else {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, docuFolUpCal);
                        }
                        break;
                    case 4:
                        if (parent.getItemAtPosition(position).toString().contentEquals("Position Call")) {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, positionCall);
                        } else {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, offeJoinCall);
                        }
                        break;
                    case 5:
                        if (parent.getItemAtPosition(position).toString().contentEquals("Offer and Joining")) {
                            actionadap = new ArrayAdapter<>(context, R.layout.spinner_item, offerJoining);
                        }
                        break;
                }
                actionspinner.setAdapter(actionadap);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.homebutton:
                getcalldesc = String.valueOf(calldesc.getText());
                sendDataServer();
                return true;
            default:
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    private void sendDataServer() {
        pb = new ProgressDialog(this);
        pb.setMessage("Log Updating...");
        pb.setCancelable(false);
        pb.create();
        pb.show();
        String serverURL = "https://resume.globalhunt.in/api/HiTechApp/UpdateCallLog";
        if (sharedpreferences.contains(user)) {
            idofuser = sharedpreferences.getString(user, "");
        }
        getsubject = subjectspinner.getSelectedItem().toString();
        getsubject = getsubject.replace('-', ' ');
        getaction = actionspinner.getSelectedItem().toString();
        StringRequest request = new StringRequest(Request.Method.POST, serverURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jobj = new JSONObject(response);
                    tab_row = jobj.getString("RecordId");
                    if (tab_row != null) {
                        Toast.makeText(getApplicationContext(), "Log Created Successfully", Toast.LENGTH_LONG).show();
                        DbAutoSave dbAutoSave = new DbAutoSave(context);
                        dbAutoSave.insertData(dateTime, tab_row);
                        Log.i("resultAddCall", dateTime + "," + tab_row);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb.cancel();
                startActivity(new Intent(context, Home_nav.class).putExtra("call", 1));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.cancel();
                Toast.makeText(getApplicationContext(), "Error: Please try again Later", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> map = new HashMap<>();
                map.put("hitechApiKey", "hitechApiX@123#");
                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                map.put("UserId", idofuser);
                map.put("DateStart", dateTime);
                map.put("CallDuration", callDurationn);
                map.put("Direction", dirrr);
                map.put("Subject", getsubject);
                map.put("Description", getcalldesc);
                map.put("Mobile", ggg);
                map.put("CallAction", getaction);
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(context).addToRequestQueue(request);
    }

    public void longOperation() {
        ggg = String.valueOf(mobile.getText());
        ggg = ggg.trim();
        ggg = ggg.replace("+91", "");
        String s_l = "https://resume.globalhunt.in/api/HiTechApp/ContactDetailByMobileNo?mobileNo=" + ggg;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, s_l, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pb.cancel();
                try {
                    c_n = new JSONObject(response).getString("Name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (c_n != null) {
                    EditText contactperson = (EditText) findViewById(R.id.contactperson);
                    contactperson.setText(c_n);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.cancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> cmap = new HashMap<>();
                cmap.put("hitechApiKey", "hitechApiX@123#");
                cmap.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return cmap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(context).addToRequestQueue(stringRequest);
    }


    private void getRecentCallDetails() {
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<StringBuffer> list = new ArrayList<StringBuffer>();
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        if (perm != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (managedCursor == null) {
            return;
        }
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        stringBuffer.append("Call Log :");
        managedCursor.moveToLast();
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        Date callDayTime = new Date(Long.valueOf(callDate));
        String callDuration = managedCursor.getString(duration);
        String dir = null;
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
        stringBuffer.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(callDayTime).append(" \nCall duration in sec :--- ").append(callDuration);
        list.add(stringBuffer);

        String[] toStoreData;
        String[] toStoreData1;
        if ((list.get(list.size() - 1).toString()).contains("Phone Number")) {
            toStoreData = (list.get(list.size() - 1).toString()).split("Phone Number:+");
            String[] PhoneNo = toStoreData[1].split("Call Type:");
            phoneNos = PhoneNo[0].replace("---", "");
            callTypes = PhoneNo[1].replace("---", "");
            toStoreData1 = (list.get(list.size() - 1).toString()).split("Call Date:");
            String[] CallDuration = toStoreData1[1].split("Call duration in sec");
            callDates = CallDuration[0].replace("---", "");
            callDurations = CallDuration[1].replace(":---", "");
        }
        getPhoneNos();
        managedCursor.close();
    }

    public void setPhoneNo(String phoneNos) {
        this.phoneNos = phoneNos;
    }

    public String getPhoneNos() {
        return phoneNos;
    }

    public void setCallTypes(String callTypes) {
        this.callTypes = callTypes;
    }

    public String getCallTypes() {
        return callTypes;
    }


}