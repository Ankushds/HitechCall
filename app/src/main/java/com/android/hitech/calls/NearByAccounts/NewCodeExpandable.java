
package com.android.hitech.calls.NearByAccounts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.android.hitech.calls.R;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCodeExpandable extends AppCompatActivity {
    String distance = "10", latitude = "28.5356727", longitude = "77.2674794";
    ActionBar actionBar;
    ProgressDialog pb;
    List<String> listHeader = new ArrayList<>();
    List<String> list1 = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    List<String> con_no = new ArrayList<>();
    List<JSONArray> add_list = new ArrayList<>();
    JSONArray jsonArray;
    ExpandableListView expandList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_code_expnd);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("My Accounts");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pb = new ProgressDialog(this);
        pb.setMessage("Log Updating...");
        pb.setCancelable(false);
        pb.create();
        pb.show();
        String url = "http://192.168.12.48/api/HiTechApp/GetAccountDetail?distance=" + distance + "&lat=" + latitude + "&longitude=" + longitude;
        StringRequest jrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        add_list.add(jsonArray.getJSONObject(i).getJSONArray("AccountAddress"));
                        listHeader.add(jsonArray.getJSONObject(i).getString("AccountName"));
                        list1.add(jsonArray.getJSONObject(i).getString("ContactNo"));
                        list2.add(jsonArray.getJSONObject(i).getString("IsAccount"));
                        con_no.add(jsonArray.getJSONObject(i).getString("ContactPerson"));
                    }
                    expandList = (ExpandableListView) findViewById(R.id.expandList);
                    Log.i("MEASURE_SPEC",expandList.getMeasuredHeightAndState()+" , "+expandList.getMeasuredHeightAndState());
                    expandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        int previousGroup = -1;

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            if (groupPosition != previousGroup) {
                                expandList.collapseGroup(previousGroup);
                                previousGroup = groupPosition;
                            }
                        }
                    });
                    expandList.setAdapter(new ExpandAdapter(NewCodeExpandable.this, listHeader, list1, list2, con_no, add_list));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb.cancel();
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
                Map<String, String> map = new HashMap<>();
                map.put("hitechApiKey", "hitechApiX@123#");
                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                return map;
            }
        };
        jrequest.setRetryPolicy(new DefaultRetryPolicy(50000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(getBaseContext()).addToRequestQueue(jrequest);
    }
}
