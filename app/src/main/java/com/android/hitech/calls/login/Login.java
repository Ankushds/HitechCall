package com.android.hitech.calls.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.android.hitech.calls.R;
import com.android.hitech.calls.homepage.Home_nav;
import com.android.hitech.calls.homepage.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public class Login extends AppCompatActivity {
    String nameValue;
    Button login;
    EditText Password;
    AutoCompleteTextView UserName;
    SessionManager session;
    String serverURL;
    NetworkInfo nInfo;
    SharedPreferences sharedpreferences;
    final String mypreference = "mypref";
    final String Name = "nameKey";
    final String user = "userKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);
        login = (Button) findViewById(R.id.btnLogin);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        nInfo = cm.getActiveNetworkInfo();
        session = new SessionManager();
        sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);
        UserName = (AutoCompleteTextView) findViewById(R.id.edtUserName);
        Password = (EditText) findViewById(R.id.edtPassword);
        if (sharedpreferences.contains("user_sug")){
            Set<String> set1 = sharedpreferences.getStringSet("user_sug",null);
            if (!set1.isEmpty()){
                String[] objects = set1.toArray(new String[]{});
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,objects);
                UserName.setAdapter(adapter1);
            }
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = UserName.getText().toString();
                if (sharedpreferences.contains("user_sug")){
                    Set<String> set = sharedpreferences.getStringSet("user_sug",null);
                    if (set!=null){
                        if (!set.contains(u)){
                            set.add(u);
                        }
                    }
                }else{
                    Set<String> set =new HashSet<String>();
                    set.add(u);
                    sharedpreferences.edit().putStringSet("user_sug",set).apply();
                }
                String p = Password.getText().toString();
                if (u.length() != 0 && p.length() != 0) {
                    if (nInfo != null && nInfo.isConnected()) {
                        serverURL = "https://resume.globalhunt.in/api/HiTechApp/LogIn?username=" + u + "&password=" + p;
                        new LongOperation().execute(serverURL);
                    } else {
                        new AlertDialog.Builder(Login.this)
                                .setCancelable(false)
                                .setTitle("No Active Internet Connection")
                                .setMessage("Switch on the Internet or Connect to active WIFI")
                                .setNegativeButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } else {
                    Toast.makeText(Login.this, "Please Enter Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private class LongOperation extends AsyncTask<String, String, String> {
        StringBuilder sb;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this,R.style.alertL);
            pd.setMessage("Please wait...");
            pd.create();
            pd.show();
        }

        protected String doInBackground(String... urls) {
            BufferedReader reader;
            sb = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("hitechApiKey", "hitechApiX@123#");
                conn.setRequestProperty("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception ex) {
                return "Login failed";
            }
        }

        protected void onPostExecute(String s) {
            pd.cancel();
            if (s != "Login failed") {
                try {
                    JSONObject jsonRootObject = new JSONObject(s);
                    String uid = jsonRootObject.getString("UserId");
                    nameValue = jsonRootObject.getString("UserName");
                    session.setPreferences(getApplicationContext(), "status", "1");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Name, nameValue);
                    editor.putString(user, uid);
                    editor.apply();
                    Intent i = new Intent(Login.this, Home_nav.class);
                    i.putExtra("username", nameValue);
                    i.putExtra("userid", uid);
                    startActivity(i);
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Error:Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
            }
        }
    }

}





