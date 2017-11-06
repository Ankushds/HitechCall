package com.android.hitech.calls.NearByAccounts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.hitech.calls.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

class CusListAdapter extends BaseExpandableListAdapter  {
    Context context;
    List<String> set_add = new ArrayList<>();
    JSONArray addressArray;

    public CusListAdapter(Context context, List<JSONArray> add_list, int groupPosition) {
        this.context = context;
        addressArray = add_list.get(groupPosition);
        for (int i = 0; i < addressArray.length(); i++) {
            try {
                set_add.add(addressArray.getJSONObject(i).getString("Addressname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return addressArray.length();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return addressArray;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        System.out.print("ThisisBoolean :"+isExpanded);
        TextView tv = new TextView(context);
        tv.setPadding(50,10,0,10);
        if (isExpanded){
            tv.setText("LESS..");
        }else{
            tv.setText("MORE..");
        }
        tv.setTextSize(18);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_START);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(Color.parseColor("#cc5200"));
        tv.setLayoutParams(lp);
        return tv;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.second_list, null,false);
        TextView txv = (TextView) v.findViewById(R.id.sec_list1);
        txv.setText(set_add.get(childPosition));
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
