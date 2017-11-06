package com.android.hitech.calls.splash;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.hitech.calls.R;
import com.android.hitech.calls.homepage.Home_nav;
import com.android.hitech.calls.homepage.SessionManager;
import com.android.hitech.calls.login.MainPagerActivity;


public class SplashScreen extends AppCompatActivity {
    SessionManager session;
    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.CALL_PHONE,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int perm1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int perm2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        int perm3 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (perm != PackageManager.PERMISSION_GRANTED || perm1 != PackageManager.PERMISSION_GRANTED || perm2 != PackageManager.PERMISSION_GRANTED || perm3 != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permission, 7882);
        } else {
            goNext();
        }
    }

    private void goNext() {
        session = new SessionManager();
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    String status = session.getPreferences(SplashScreen.this, "status");
                    if (status.equals("1")) {
                        Intent intent = new Intent(SplashScreen.this, Home_nav.class);
                        startActivity(intent);
                    } else {
                        Intent i = new Intent(SplashScreen.this, MainPagerActivity.class);
                        startActivity(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        goNext();
    }
}

