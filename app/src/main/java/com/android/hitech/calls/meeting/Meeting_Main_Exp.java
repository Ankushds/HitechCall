package com.android.hitech.calls.meeting;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hitech.calls.R;
import com.android.hitech.calls.database.DbAutoSave;
import com.android.hitech.calls.splash.MyNetwork;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Meeting_Main_Exp extends AppCompatActivity {
    ActionBar actionBar;
    RecyclerView meet_rc;
    public static final String mypreference = "mypref";
    public static final String MEET_TAG = "MeetingMainExp";
    public static final String user = "userKey";
    List<String> accountName = new ArrayList<>();
    List<String> contactPerson = new ArrayList<>();
    List<String> locationList = new ArrayList<>();
    List<Long> startDate = new ArrayList<>();
    List<String> endDate = new ArrayList<>();
    List<String> meetingId = new ArrayList<>();
    String serverUserId;
    SharedPreferences sf, sf1;
    DbAutoSave autoSave;
    private final Context myContext = Meeting_Main_Exp.this;
    ProgressDialog pd;
    LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_main);
        sf1 = getSharedPreferences(mypreference, MODE_PRIVATE);
        sf = PreferenceManager.getDefaultSharedPreferences(myContext);
        serverUserId = sf1.getString(user, null);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("My Mettings");
        }
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder d = new AlertDialog.Builder(myContext, R.style.alertDialogstyle);
            d.setMessage("You need to activate location service to use this feature. Please turn on network or GPS mode in location settings");
            d.setCancelable(false);
            d.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            d.create();
            d.show();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        pd = new ProgressDialog(myContext);
        pd.setCancelable(false);
        pd.show();
        pd.setMessage("Please wait...");
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, "https://resume.globalhunt.in/api/HiTechApp/MeetingListByUserId?userId=" + serverUserId, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        contactPerson.add(response.getJSONObject(i).getString("ContactPerson"));
                        accountName.add(response.getJSONObject(i).getString("Accountname"));
                        locationList.add(response.getJSONObject(i).getString("MeetingLocation"));
                        meetingId.add(response.getJSONObject(i).getString("MeetingId"));
                        String d_start = response.getJSONObject(i).getString("MeetingDate").replace('T', ' ');
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        startDate.add(sdf.parse(d_start).getTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                meet_rc = (RecyclerView) findViewById(R.id.meet_rc);
                meet_rc.setLayoutManager(new LinearLayoutManager(myContext));
                meet_rc.setAdapter(new MeetAdapter());
                pd.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myContext, "Record not Found", Toast.LENGTH_LONG).show();
                pd.cancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> map = new HashMap<>();
                map.put("userId", "c2c0acd4-d459-4da4-8d16-e3d84ad40a9d");
                map.put("Authorization", "Basic YW5kcm9pZDpoaXRlY2hBcGlYQDEyMyM=");
                map.put("hitechApiKey", "hitechApiX@123#");
                return map;
            }
        };
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyNetwork.getInstance(myContext).addToRequestQueue(arrayRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    private class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(myContext);
            View view = inflater.inflate(R.layout.meeting_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.maccountName.setText(accountName.get(position));
            holder.mcontactperson.setText(contactPerson.get(position));
            holder.mlocation.setText(locationList.get(position));
            long dc = startDate.get(position);
            autoSave = new DbAutoSave(myContext);
            SQLiteDatabase db = autoSave.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from meetings where StartDate=" + dc, null);
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                String dbString = cursor.getString(5);
                if (dbString != null) {
                    holder.meetStatus.setText("Tracked");
                } else {
                    holder.meetStatus.setText("Not Tracked");
                }
            } else {
                holder.meetStatus.setText("Not Tracked");
            }
            cursor.close();
            String s_t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dc);
            holder.mstartDate.setText(s_t);
            if (position < endDate.size()) {
                holder.mendDate.setText(endDate.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return accountName.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            TextView maccountName, mcontactperson, mlocation, mstartDate, mendDate, meetStatus, textMenu;
            AlertDialog dialog;
            double latitude, longitude, desLat, desLng;
            Geocoder geocoder;

            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                maccountName = (TextView) itemView.findViewById(R.id.maccountName);
                mcontactperson = (TextView) itemView.findViewById(R.id.mcontactperson);
                mlocation = (TextView) itemView.findViewById(R.id.mlocation);
                mstartDate = (TextView) itemView.findViewById(R.id.mstartDate);
                mendDate = (TextView) itemView.findViewById(R.id.mendDate);
                meetStatus = (TextView) itemView.findViewById(R.id.meetStatus);
                textMenu = (TextView) itemView.findViewById(R.id.textMenu);
                textMenu.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.yesBtn:
                        dialog.cancel();
                        new ProgressDialog(myContext);
                        pd.setCancelable(false);
                        pd.setMessage("Please wait...");
                        pd.show();
                        checkLocation();
                        break;
                    case R.id.noBtn:
                        dialog.cancel();
                        break;
                    case R.id.textMenu:
                        PopupMenu popupMenu = new PopupMenu(myContext, v);
                        popupMenu.getMenu().add(0, R.id.infoItem, 0, "Detail Info");
                        popupMenu.getMenu().add(0, R.id.stopMeet, 0, "Stop Meeting");
                        popupMenu.setOnMenuItemClickListener(this);
                        popupMenu.show();
                        break;
                    default:
                        dialog = new MyAlertDialog(v.getContext());
                        dialog.setCancelable(false);
                        LayoutInflater inflater = LayoutInflater.from(myContext);
                        View view = inflater.inflate(R.layout.meeting_start, null);
                        view.findViewById(R.id.noBtn).setOnClickListener(this);
                        view.findViewById(R.id.yesBtn).setOnClickListener(this);
                        dialog.setView(view);
                        dialog.show();
                }
            }

            private void checkLocation() {
                lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
            }

            private void doHere(Location location, final int position) {
                getDestinationAdd();
                String urls = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + desLat + "," + desLng + "&key=AIzaSyAu-PVvLUnD0iggdqxJt8a6tak4O6rdGu0";
                StringRequest request = new StringRequest(Request.Method.GET, urls, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.cancel();
                        Log.i(MEET_TAG, response);
                        /*try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").contentEquals("OK")) {
                                JSONArray array = object.getJSONArray("rows");
                                JSONArray array1 = array.getJSONObject(0).getJSONArray("elements");
                                String duration = array1.getJSONObject(0).getJSONObject("duration").getString("text");
                                long d0 = startDate.get(position);
                                if (duration.contains("hours") && duration.contains("mins")) {
                                    duration = duration.replace("hours", ":").replace("mins", "");
                                    String[] sArray = duration.split(":");
                                    int hours = Integer.parseInt(sArray[0].trim());
                                    int minutes = Integer.parseInt(sArray[1].trim());
                                    long compare_time = hours * 60 * 60 * 1000 + minutes * 60 * 1000 + 30 * 60 * 1000;
                                    if (d0 - System.currentTimeMillis() < compare_time && d0 >= System.currentTimeMillis()) {
                                        sf.edit().putLong("TravelStartTime", System.currentTimeMillis()).apply();
                                        sf.edit().putString("MeetingId", meetingId.get(position)).apply();
                                        sf.edit().putLong("datakey", startDate.get(position)).apply();
                                        Intent intent = new Intent(myContext, MyMeetingService.class);
                                        intent.putExtra("targetAddress", locationList.get(position));
                                        if (!isMyServiceRunning(MyMeetingService.class)) {
                                            android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(myContext)
                                                    .setSmallIcon(android.R.color.holo_red_dark)
                                                    .setColor(android.R.color.holo_red_dark)
                                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.fmeetstar))
                                                    .setContentTitle("My Meeting")
                                                    .setContentText("Meeting started!").setOngoing(true);
                                            Intent resultIntent = new Intent(myContext, Meeting_Main_Exp.class);
                                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(myContext);
                                            stackBuilder.addNextIntent(resultIntent);
                                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                            mBuilder.setContentIntent(resultPendingIntent);
                                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.notify(7882, mBuilder.build());
                                            startService(intent);
                                        } else {
                                            Toast.makeText(myContext, "One Meeting Already Started", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        final MyAlertDialog alertDialog = new MyAlertDialog(myContext, R.style.alertDialogstyle);
                                        alertDialog.setTitle("! Alert");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("Meeting could not be started right now, Either Current time is more than expected time or meeting date is of past date.");
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.cancel();
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                } else if (duration.contains("mins")) {
                                    duration = duration.replace("mins", "");
                                    int minutes = Integer.parseInt(duration.trim());
                                    long compare_time = minutes * 60 * 1000 + 30 * 60 * 1000;
                                    if (d0 - System.currentTimeMillis() <= compare_time && d0 >= System.currentTimeMillis()) {
                                        sf.edit().putLong("datakey", startDate.get(position)).apply();
                                        sf.edit().putLong("TravelStartTime", System.currentTimeMillis()).apply();
                                        sf.edit().putString("MeetingId", meetingId.get(position)).apply();
                                        Intent intent = new Intent(myContext, MyMeetingService.class);
                                        intent.putExtra("targetAddress", locationList.get(position));
                                        if (!isMyServiceRunning(MyMeetingService.class)) {
                                            android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(myContext)
                                                    .setSmallIcon(android.R.color.holo_red_dark)
                                                    .setColor(android.R.color.holo_red_dark)
                                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.fmeetstar))
                                                    .setContentTitle("My Meeting")
                                                    .setContentText("Meeting started!").setOngoing(true);
                                            Intent resultIntent = new Intent(myContext, Meeting_Main_Exp.class);
                                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(myContext);
                                            stackBuilder.addNextIntent(resultIntent);
                                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                            mBuilder.setContentIntent(resultPendingIntent);
                                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.notify(7882, mBuilder.build());
                                            startService(intent);
                                        } else {
                                            Toast.makeText(myContext, "One Meeting Already Started", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        final MyAlertDialog alertDialog = new MyAlertDialog(myContext, R.style.alertDialogstyle);
                                        alertDialog.setTitle("! Alert");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage("Meeting could not be started right now, Either Current time is more than expected time or meeting date is of past date.");
                                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                alertDialog.cancel();
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                }
                            } else {
                                Toast.makeText(myContext, "Invalid address try again", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.i(MEET_TAG, String.valueOf(e));
                            e.printStackTrace();
                        }*/
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Toast.makeText(myContext, "Invalid address try again", Toast.LENGTH_LONG).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(20000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MyNetwork.getInstance(myContext).addToRequestQueue(request);
            }

            private void getDestinationAdd() {
                geocoder = new Geocoder(myContext);
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationList.get(getLayoutPosition()), 1);
                    Address address = addressList.get(0);
                    Log.i(MEET_TAG, String.valueOf(address));
                    desLat = address.getLatitude();
                    desLng = address.getLongitude();
                    Log.i(MEET_TAG, desLat + " , " + desLng);
                } catch (IOException e1) {
                    Log.i(MEET_TAG, "" + e1);
                }
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

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    doHere(location, getLayoutPosition());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.infoItem:
                        Intent i = new Intent(myContext, ShowTrackingData.class);
                        i.putExtra("account", accountName.get(getLayoutPosition()));
                        i.putExtra("contact", contactPerson.get(getLayoutPosition()));
                        i.putExtra("location", locationList.get(getLayoutPosition()));
                        i.putExtra("sdate", startDate.get(getLayoutPosition()));
                        startActivity(i);
                        break;
                    case R.id.stopMeet:
                        if (isMyServiceRunning(MyMeetingService.class)) {
                            MyAlertDialog d = new MyAlertDialog(myContext, R.style.alertDialogstyle);
                            d.setTitle("!Alert");
                            d.setMessage("Are you want to stop running Metting.");
                            d.setCancelable(false);
                            d.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dg, int which) {
                                    DbAutoSave db = new DbAutoSave(myContext);
                                    SQLiteDatabase database = db.getWritableDatabase();
                                    Cursor cursor = database.rawQuery("select * from meetings where StartDate=" + sf.getLong("datakey", -1), null);
                                    if (cursor.moveToLast()) {
                                        stopService(new Intent(myContext, MyMeetingService.class));
                                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        nm.cancel(7882);
                                        dg.cancel();
                                        Toast.makeText(myContext, "Meeting Finished", Toast.LENGTH_LONG).show();
                                    } else {
                                        dg.cancel();
                                        MyAlertDialog d1 = new MyAlertDialog(myContext, R.style.alertDialogstyle);
                                        d1.setTitle("!Alert");
                                        d1.setMessage("One Metting is currently running, You can't stop this meeting until it complete");
                                        d1.setCancelable(false);
                                        d1.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dog, int which) {
                                                dog.cancel();
                                            }
                                        });
                                        d1.show();
                                    }
                                    cursor.close();
                                }
                            });
                            d.show();

                        } else {
                            Toast.makeText(myContext, "No Meeting Currently Running", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return false;
            }
        }

        private class MyAlertDialog extends AlertDialog {


            MyAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
                super(context, themeResId);
            }

            MyAlertDialog(@NonNull Context context) {
                super(context);
            }
        }
    }
}


