package com.android.hitech.calls.meeting;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.hitech.calls.R;
import com.android.hitech.calls.database.DbAutoSave;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ShowTrackingData extends AppCompatActivity {
    SimpleDateFormat sdf;
    long l, l1, l2, l3, l5 = 0;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_trac_data);
        for (int i=0;i<8;i++) System.out.println(i);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("My Mettings");
        }
        Intent intent = getIntent();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SQLiteDatabase db = new DbAutoSave(this).getReadableDatabase();
        ((TextView) findViewById(R.id.tmaccountName)).setText(intent.getStringExtra("account"));
        ((TextView) findViewById(R.id.tmcontactperson)).setText(intent.getStringExtra("contact"));
        ((TextView) findViewById(R.id.tmlocation)).setText(intent.getStringExtra("location"));
        l = intent.getLongExtra("sdate", 0);
        ((TextView) findViewById(R.id.tmstartDate)).setText(sdf.format(l));
        Cursor cursor = db.rawQuery("select * from meetings where StartDate=" + l, null);
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            ((TextView) findViewById(R.id.tmeetId)).setText(cursor.getString(2));
            l1 = cursor.getLong(3);
            ((TextView) findViewById(R.id.tmeetstarted)).setText(sdf.format(l1));
            l2 = cursor.getLong(4);
            l3 = l1 - l2;
            ((TextView) findViewById(R.id.ttravelstart)).setText(sdf.format(l3));
            ((TextView) findViewById(R.id.ttravelstop)).setText(sdf.format(l1));
            ((TextView) findViewById(R.id.ttraveldur)).setText(l2 / 60000 + " minutes");
            l5 = cursor.getLong(5);
            if (l5 != 0) {
                ((TextView) findViewById(R.id.tmeetstop)).setText(sdf.format(l5));
                ((TextView) findViewById(R.id.cmt)).setText((l5 - l1) / 60000 + "minutes");
                ((TextView) findViewById(R.id.ttmt)).setText((l2 + (l5 - l1)) / 60000 + "minutes");
                ((TextView) findViewById(R.id.tmeetStatus)).setText("Tracked");
            }
        } else {
            ((TextView) findViewById(R.id.tmeetStatus)).setText("Not Tracked");
        }
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}
