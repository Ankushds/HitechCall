package com.android.hitech.calls.NearByAccounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.hitech.calls.R;
import org.json.JSONArray;
import java.util.List;

class ExpandAdapter extends BaseExpandableListAdapter {
    Context context;
    List<String> listHeader, list1, list2, con_no;
    List<JSONArray> add_list;

    ExpandAdapter(Context context, List<String> listHeader, List<String> list1, List<String> list2, List<String> con_no, List<JSONArray> add_list) {
        this.context = context;
        this.listHeader = listHeader;
        this.list1 = list1;
        this.list2 = list2;
        this.con_no = con_no;
        this.add_list = add_list;
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
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
    public View getGroupView(int groupPosition, boolean isExpanded, View v, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_group, null);
        TextView expandGroup = (TextView) convertView.findViewById(R.id.expandGroup);
        expandGroup.setText(listHeader.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View v, ViewGroup parent) {

        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
        CusExpList cusExpList = new CusExpList(context);
        Drawable d = context.getResources().getDrawable(R.drawable.settings,context.getTheme());
        cusExpList.setGroupIndicator(d);
        cusExpList.setAdapter(new CusListAdapter(context, add_list, groupPosition));
        LinearLayout add_root = (LinearLayout) convertView.findViewById(R.id.add_root);
        add_root.addView(cusExpList);
        TextView expandItem1 = (TextView) convertView.findViewById(R.id.expandItem1);
        TextView expandItem2 = (TextView) convertView.findViewById(R.id.expandItem2);
        TextView expandItem3 = (TextView) convertView.findViewById(R.id.expandItem3);
        expandItem1.setText(list1.get(groupPosition));
        if (list2.get(groupPosition).contentEquals("0")){
            expandItem2.setText("Account");
        }else{
            expandItem2.setText("Company");
        }
        expandItem3.setText(con_no.get(groupPosition));
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
