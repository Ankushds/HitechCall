package com.android.hitech.calls.splash;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

public class MyNetwork {
    Context c;
    public static MyNetwork myNetwork;

    public static MyNetwork getInstance(Context context) {
        myNetwork = new MyNetwork(context);
        return myNetwork;
    }

    private MyNetwork(Context context) {
        this.c = context;
    }

    public void addToRequestQueue(Request<?> request) {
        Volley.newRequestQueue(c).add(request);
    }
}
