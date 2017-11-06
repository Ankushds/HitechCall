package com.android.hitech.calls.NearByAccounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ExpandableListView;

public class CusExpList extends ExpandableListView {
    public CusExpList(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(1073741823, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(1073741823, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setGroupIndicator(Drawable groupIndicator) {
        super.setGroupIndicator(groupIndicator);
    }
}
