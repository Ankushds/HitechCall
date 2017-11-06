package com.android.hitech.calls.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.hitech.calls.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Dashboard extends Fragment {
    private final long oneDay = 24 * 60 * 60 * 1000;
    HorizontalBarChart barChart;
    int dircode, d0i = 0, d0o = 0, d0m = 0, d1i = 0, d1o = 0, d1m = 0, d2i = 0, d2o = 0, d2m = 0, d3i = 0, d3o = 0, d3m = 0, d4i = 0, d4o = 0, d4m = 0, d5i = 0, d5o = 0, d5m = 0;
    long current;
    String[] legends = {"Outgoing", "Incoming", "Missed"};
    String dir, result, callType, di_m;
    int[] positions = {Color.parseColor("#99C199"), Color.parseColor("#E5E500"), Color.parseColor("#ff9933")};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        current = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.dashboard, container, false);
        barChart = (HorizontalBarChart) view.findViewById(R.id.barChart);
        getAllCalls();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        onFragStart();
    }

    public void onFragStart() {
        ArrayList<BarEntry> bargroup1 = new ArrayList<>();
        bargroup1.add(new BarEntry(d0m, 0));
        bargroup1.add(new BarEntry(d1m, 1));
        bargroup1.add(new BarEntry(d2m, 2));
        bargroup1.add(new BarEntry(d3m, 3));
        bargroup1.add(new BarEntry(d4m, 4));
        bargroup1.add(new BarEntry(d5m, 5));

        ArrayList<BarEntry> bargroup2 = new ArrayList<>();
        bargroup2.add(new BarEntry(d0i, 0));
        bargroup2.add(new BarEntry(d1i, 1));
        bargroup2.add(new BarEntry(d2i, 2));
        bargroup2.add(new BarEntry(d3i, 3));
        bargroup2.add(new BarEntry(d4i, 4));
        bargroup2.add(new BarEntry(d5i, 5));

        ArrayList<BarEntry> bargroup3 = new ArrayList<>();
        bargroup3.add(new BarEntry(d0o, 0));
        bargroup3.add(new BarEntry(d1o, 1));
        bargroup3.add(new BarEntry(d2o, 2));
        bargroup3.add(new BarEntry(d3o, 3));
        bargroup3.add(new BarEntry(d4o, 4));
        bargroup3.add(new BarEntry(d5o, 5));

        BarDataSet barDataSet1 = new BarDataSet(bargroup1, "Bar Group 1");
        barDataSet1.setColor(Color.parseColor("#ff9933"));
        BarDataSet barDataSet2 = new BarDataSet(bargroup2, "Bar Group 2");
        barDataSet2.setColor(Color.parseColor("#E5E500"));
        BarDataSet barDataSet3 = new BarDataSet(bargroup3, "Bar Group 2");
        barDataSet3.setColor(Color.parseColor("#99C199"));

        ArrayList<String> labels = new ArrayList<String>();
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        labels.add(df.format(current));
        labels.add(df.format(current - oneDay));
        labels.add(df.format(current - 2 * oneDay));
        labels.add(df.format(current - 3 * oneDay));
        labels.add(df.format(current - 4 * oneDay));
        labels.add(df.format(current - 5 * oneDay));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        dataSets.add(barDataSet3);

        BarData data = new BarData(labels, dataSets);
        barChart.setData(data);
        Legend legend = barChart.getLegend();
        legend.setCustom(positions, legends);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.animateXY(1000, 1000);
    }

    private void getAllCalls() {
        String[] projection = {
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DATE
        };
        int perm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG);
        if (perm != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " DESC");
        if (managedCursor != null) {
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int d_e = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            DateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
            while (managedCursor.moveToNext()) {
                callType = managedCursor.getString(type);
                di_m = managedCursor.getString(d_e);
                dircode = Integer.parseInt(callType);
                result = df.format(Long.parseLong(di_m));
                if (result.contentEquals(df.format(current))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d0o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d0i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d0m++;
                            break;
                    }
                } else if (result.contentEquals(df.format(current - oneDay))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d1o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d1i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d1m++;
                            break;
                    }
                } else if (result.contentEquals(df.format(current - 2 * oneDay))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d2o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d2i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d2m++;
                            break;
                    }

                } else if (result.contentEquals(df.format(current - 3 * oneDay))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d3o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d3i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d3m++;
                            break;
                    }
                } else if (result.contentEquals(df.format(current - 4 * oneDay))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d4o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d4i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d4m++;
                            break;
                    }
                } else if (result.contentEquals(df.format(current - 5 * oneDay))) {
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            d5o++;
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            d5i++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            d5m++;
                            break;
                    }
                }
            }
            managedCursor.close();
        }
    }
}
