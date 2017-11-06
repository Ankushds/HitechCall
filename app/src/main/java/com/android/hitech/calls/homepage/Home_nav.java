package com.android.hitech.calls.homepage;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.hitech.calls.Alert_popup.About;
import com.android.hitech.calls.Location.MapsActivity;
import com.android.hitech.calls.Location.Servicebackground;
import com.android.hitech.calls.R;
import com.android.hitech.calls.call_logs.Call_logs;
import com.android.hitech.calls.dashboard.Dashboard;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.login.Login;
import com.android.hitech.calls.meeting.Phonemeeting;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Home_nav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    SessionManager session;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    FragmentTransaction ft;
    Fragment fragment;
    Button btnInstall, btnCancel;
    int itemid;
    MyDialog myDialog;
    View view;
    LayoutInflater inflater;
    ProgressBar pb;
    String d_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (getIntent().hasExtra("call")) {
            int in = getIntent().getIntExtra("call", 0);
            if (in == 1) {
                tx.replace(R.id.Fragment_container, new Call_logs()).commit();
            }
        } else {
            tx.replace(R.id.Fragment_container, new Dashboard());
            tx.commit();
        }
        if (!isMyServiceRunning(Servicebackground.class)) {
            startService(new Intent(this, Servicebackground.class));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.u1);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            tv.setText(sharedpreferences.getString(Name, ""));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        boolean ret = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!sharedpreferences.getBoolean("clientsave", false)) {
            String u_id = sharedpreferences.getString("userKey", "");
            String u_l = "https://resume.globalhunt.in/api/HiTechApp/CallMissedCallList?userId=" + u_id;
            StringRequest req = new StringRequest(Request.Method.GET, u_l, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    DbAutoSave autoSave = new DbAutoSave(Home_nav.this);
                    try {
                        JSONObject j_obj = new JSONObject(response);
                        JSONArray j_array = j_obj.getJSONArray("CallList");
                        for (int i = 0; i < j_array.length(); i++) {
                            d_start = j_array.getJSONObject(i).getString("DateStart");
                            d_start = d_start.replace('T', ' ');
                            autoSave.insertData(d_start, j_array.getJSONObject(i).getString("RowId"));
                        }
                        JSONArray j_array1 = j_obj.getJSONArray("MissedCallList");
                        for (int i = 0; i < j_array1.length(); i++) {
                            d_start = j_array1.getJSONObject(i).getString("DateStart");
                            d_start = d_start.replace('T', ' ');
                            Date dt = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).parse(d_start);
                            d_start = new SimpleDateFormat("yyy-MM-dd H:mm:ss", Locale.getDefault()).format(dt);
                            autoSave.insertData(d_start, j_array1.getJSONObject(i).getString("RowId"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    super.getHeaders();
                    Map<String, String> headers = new HashMap<>();
                    headers.put("hitechApiKey", "hitechApiX@123#");
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                    return headers;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyNetwork.getInstance(Home_nav.this).addToRequestQueue(req);
            sharedpreferences.edit().putBoolean("clientsave", true).apply();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        itemid = item.getItemId();
        ft = getSupportFragmentManager().beginTransaction();
        CountDownTimer cdt = new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                switch (itemid) {
                    case R.id.nav_dashboard:
                        fragment = new Dashboard();
                        ft.replace(R.id.Fragment_container, fragment);
                        ft.commit();
                        break;
                    case R.id.nav_call:
                        fragment = new Call_logs();
                        ft.replace(R.id.Fragment_container, fragment);
                        ft.commit();
                        break;
                    case R.id.nav_location:
                        Intent i = new Intent(Home_nav.this, MapsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.c_t_mt:
                        Intent i2 = new Intent(Home_nav.this, Phonemeeting.class);
                        startActivity(i2);
                        break;
                    case R.id.nav_schedule:
                        Toast.makeText(Home_nav.this, "Currently this option not Enabled for you.", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_about:
                        fragment = new About();
                        ft.replace(R.id.Fragment_container, fragment);
                        ft.commit();
                        break;
                    case R.id.nav_logout:
                        session.setPreferences(Home_nav.this, "status", "0");
                        Intent in = new Intent(Home_nav.this, Login.class);
                        startActivity(in);
                }
            }
        };
        cdt.start();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        item.setChecked(true);
        inflater = LayoutInflater.from(this);
        switch (item.getItemId()) {
            case R.id.update:
                updateOperation();
                return true;
            case R.id.versionn:
                myDialog = new MyDialog(this);
                View v = inflater.inflate(R.layout.version, null, false);
                myDialog.setCancelable(false);
                myDialog.setView(v);
                myDialog.show();
                myDialog.findViewById(R.id.verCancel).setOnClickListener(this);
                insertVersion(((TextView) v.findViewById(R.id.verUse)).getText().toString());
                return true;
            case R.id.other_app:
                Toast.makeText(this, "Currently this option not Enabled for you.", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }

    private void insertVersion(String version) {
        String idofuser = sharedpreferences.getString("userKey", "");
        version = version.replace("Version -","").trim();
        StringRequest request = new StringRequest(Request.Method.POST, "https://resume.globalhunt.in/api/HiTechApp/InsertUpdateAppverion?uid=" + idofuser + "&version=" + version, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("indiaindia", response);
        }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("indiaindia", "" + error);
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
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void updateOperation() {
        myDialog = new MyDialog(this);
        view = inflater.inflate(R.layout.update, null);
        myDialog.setView(view);
        myDialog.setCancelable(false);
        myDialog.show();
        btnInstall = (Button) myDialog.findViewById(R.id.btnInstall);
        btnCancel = (Button) myDialog.findViewById(R.id.cancel);
        if (btnInstall != null) {
            btnInstall.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnInstall) {
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.root);
            pb = (ProgressBar) inflater.inflate(R.layout.customprogressbar, null, false);
            if (ll.getChildCount() < 3) {
                ll.addView(pb, 1);
            }

            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new UpdateTask().execute("https://resume.globalhunt.in/location.apk");
                btnInstall.setVisibility(View.INVISIBLE);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 78);
            }
        } else {
            myDialog.cancel();
        }
    }

    private class MyDialog extends AlertDialog {

        MyDialog(@NonNull Context context) {
            super(context);
        }
    }

    private class UpdateTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.connect();
                int lenghtOfFile = c.getContentLength();
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
                File file = new File(dir);
                file.mkdirs();
                File outputFile = new File(file, "location.apk");
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                long total = 0;
                int len1;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + "location.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (FileNotFoundException fnfe) {

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pb.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        new UpdateTask().execute("https://resume.globalhunt.in/location.apk");
        btnInstall.setVisibility(View.INVISIBLE);
    }
}
