package com.android.hitech.calls.call_logs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Jsonsave extends JSONObject {
    public JSONObject makeJSONObject(String title, String desc, String dura, String timeee,String numbb) {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
            try {
                obj.put("userid", title);
                obj.put("call_start", desc);
                obj.put("duration", dura);
                obj.put("direction", timeee);
                obj.put("mob", numbb);
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return obj;
    }
}