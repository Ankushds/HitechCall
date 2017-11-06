package com.android.hitech.calls.meeting;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.hitech.calls.NearByAccounts.NewCodeExpandable;
import com.android.hitech.calls.R;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Phonemeeting extends AppCompatActivity implements View.OnClickListener {
    final String mypreference = "mypref";
    final String user = "userKey";
    AutoCompleteTextView editPhoneNum;
    MultiAutoCompleteTextView userAttendee;
    String contactId, contactName, accountName, fullAddress, accountId, userId, dateStart = "", dateEnd = "", line, sendData;
    List<String> userAttendent = new ArrayList<>();
    ProgressDialog pd;
    TextView txtStartDate, txtStrtTime, txtEndDate, txtEndTime;
    SharedPreferences sf;
    String[] items;
    byte[] fData;
    Calendar calendar;
    EditText edtAccNamep, edtDestinap, edtcontPersonp;
    LinearLayout meetingg, schedulemeeting, nerabyaccountss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonemeeting);
        edtAccNamep = (EditText) findViewById(R.id.edtAccNamep);
        edtcontPersonp = (EditText) findViewById(R.id.edtcontPersonp);
        edtDestinap = (EditText) findViewById(R.id.edtDestinap);
        attendingUsers();
        getUsertData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        sf = getSharedPreferences(mypreference, MODE_PRIVATE);
        userId = sf.getString(user, null);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtStartDate.setOnClickListener(this);
        txtStrtTime = (TextView) findViewById(R.id.txtStrtTime);
        txtStrtTime.setOnClickListener(this);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        txtEndDate.setOnClickListener(this);
        txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        txtEndTime.setOnClickListener(this);
        Button buttonnSubmitp = (Button) findViewById(R.id.buttonnSubmitp);
        buttonnSubmitp.setOnClickListener(this);
        meetingg = (LinearLayout) findViewById(R.id.meetingg);
        meetingg.setOnClickListener(this);
        schedulemeeting = (LinearLayout) findViewById(R.id.schedulemeeting);
        schedulemeeting.setOnClickListener(this);
        nerabyaccountss = (LinearLayout) findViewById(R.id.nerabyaccountss);
        nerabyaccountss.setOnClickListener(this);
    }

    private void getUsertData() {
        editPhoneNum = (AutoCompleteTextView) findViewById(R.id.editPhoneNum);
        editPhoneNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String mobile = editPhoneNum.getText().toString();
                    mobile = mobile.replace("+91", "");
                    mobile = mobile.trim();
                    if (mobile.length() == 10) {
                        pd = new ProgressDialog(Phonemeeting.this);
                        pd.setMessage("Please wait...");
                        pd.setCancelable(false);
                        pd.create();
                        pd.show();
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://resume.globalhunt.in/api/HiTechApp/ContactDetail?mobileNo=" + mobile, new JSONObject(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response != null) {
                                    try {
                                        contactId = response.getString("ContactId");
                                        contactName = response.getString("ContactName");
                                        accountName = response.getString("AccountName");
                                        accountId = response.getString("AccountId");
                                        fullAddress = response.getString("FullAddress");
                                        Log.i("onlytesting", accountName + "," + contactName + "," + fullAddress);
                                        edtAccNamep.setText(accountName);
                                        edtcontPersonp.setText(contactName);
                                        edtDestinap.setText(fullAddress);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(Phonemeeting.this, "No match found", Toast.LENGTH_LONG).show();
                                }
                                pd.cancel();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("aaaaaaaa", "Hello" + error);
                                pd.cancel();
                                Toast.makeText(Phonemeeting.this, "Error : Please try again later", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                super.getHeaders();
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("hitechApiKey", "hitechApiX@123#");
                                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                                return map;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        MyNetwork.getInstance(Phonemeeting.this).addToRequestQueue(request);
                    } else {
                        Toast.makeText(Phonemeeting.this, "Please enter an valid number", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }

        });
    }

    private void attendingUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, "https://resume.globalhunt.in/api/HiTechApp/InternalUserList", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String attendee = jsonArray.getJSONObject(i).getString("UserName");
                        userAttendent.add(attendee);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userAttendee = (MultiAutoCompleteTextView) findViewById(R.id.userAttendee);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Phonemeeting.this, android.R.layout.simple_dropdown_item_1line, userAttendent);
                userAttendee.setAdapter(adapter);
                userAttendee.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> map = new HashMap<String, String>();
                map.put("hitechApiKey", "hitechApiX@123#");
                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(Phonemeeting.this).addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.txtStartDate:
                startDate();
                break;
            case R.id.txtStrtTime:
                startTime();
                break;
            case R.id.txtEndDate:
                endDate();
                break;
            case R.id.txtEndTime:
                endTime();
                break;
            case R.id.buttonnSubmitp:
                afterSubmit();
                break;
            case R.id.meetingg:
                startActivity(new Intent(Phonemeeting.this, Meeting_Main_Exp.class));
                break;
            case R.id.schedulemeeting:
                Toast.makeText(this, "You can schedule meeting from here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nerabyaccountss:
                startActivity(new Intent(Phonemeeting.this, NewCodeExpandable.class));
                break;
        }
    }

    private void endDate() {
        DatePickerDialog datePicker1 = new DatePickerDialog(this, android.support.design.R.style.Base_Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                txtEndDate.setText("" + year + "-" + month + "-" + dayOfMonth);
                dateEnd = dateEnd + " " + year + "-" + month + "-" + dayOfMonth;
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker1.create();
        datePicker1.show();
    }

    private void startTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Phonemeeting.this, android.support.design.R.style.Base_Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                txtStrtTime.setText("" + hourOfDay + ":" + minute + ":00");
                dateStart = dateStart + " " + hourOfDay + ":" + minute + ":00";
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.create();
        timePickerDialog.show();
    }

    private void startDate() {
        DatePickerDialog datePicker = new DatePickerDialog(this, android.support.design.R.style.Base_Theme_AppCompat_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                txtStartDate.setText("" + year + "-" + month + "-" + dayOfMonth);
                dateStart = dateStart + " " + year + "-" + month + "-" + dayOfMonth;
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.create();
        datePicker.show();
    }

    private void endTime() {
        TimePickerDialog timePickerDialog1 = new TimePickerDialog(Phonemeeting.this, android.support.design.R.style.Base_Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                txtEndTime.setText("" + hourOfDay + ":" + minute + ":00");
                dateEnd = dateEnd + " " + hourOfDay + ":" + minute + ":00";
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog1.create();
        timePickerDialog1.show();
    }

    private void afterSubmit() {
        if (dateStart.equals("") || accountName.equals("null") || contactName.equals("null") || fullAddress.equals("null")) {
            Toast.makeText(this, "Please fill all and correct details", Toast.LENGTH_SHORT).show();
        } else {
            userAttendee = (MultiAutoCompleteTextView) findViewById(R.id.userAttendee);
            if (!userAttendee.getText().toString().equals("")) {
                items = userAttendee.getText().toString().split(",");
                submitData();
            } else {
                Toast.makeText(this, "User Attending Couldn't be blanked", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void submitData() {
        new AsyncTask<Void, Void, String>() {
            StringBuilder sb;
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(Phonemeeting.this);
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.create();
                pd.show();
                JSONObject root = new JSONObject();
                try {
                    root.put("contact_id_c", contactId);
                    root.put("relate_account_id", accountId);
                    root.put("user_id", userId);
                    root.put("location", fullAddress);
                    root.put("date_start", dateStart);
                    JSONArray jsonArray = new JSONArray();
                    for (String item : items) {
                        jsonArray.put(item);
                    }
                    root.put("AttendiesList", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendData = root.toString();
                fData = sendData.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected String doInBackground(Void... params) {
                String server_address = "https://resume.globalhunt.in/api/HiTechApp/CreateMeeting";
                sb = new StringBuilder();
                try {
                    URL url = new URL(server_address);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("hitechApiKey", "hitechApiX@123#");
                    conn.setRequestProperty("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setConnectTimeout(20000);
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(fData);
                    wr.flush();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                } catch (MalformedURLException e) {
                    return "Some Error occured";
                } catch (IOException ex) {
                    return "Some Error occured";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i("aaaaaaaa", "Hello" + s);
                pd.cancel();
                try {
                    JSONObject jObject = new JSONObject(s);
                    String status = jObject.getString("Status");
                    if (status.contentEquals("true")) {
                        Toast.makeText(Phonemeeting.this, "Metting Created Successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Phonemeeting.this, Meeting_Main_Exp.class));
                    } else {
                        Toast.makeText(Phonemeeting.this, "Error Please try again later", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(Phonemeeting.this, "Error Please try again later", Toast.LENGTH_LONG).show();
                }
                dateStart = "";
            }
        }.execute();
    }
}
