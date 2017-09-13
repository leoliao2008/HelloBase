package com.skycaster.hellobase.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class MaxHeightListView extends ListView {
    public MaxHeightListView(Context context) {
        this(context,null);
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaxHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
