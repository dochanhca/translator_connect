package com.example.translateconnector.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by ton on 4/7/18.
 */

public class ListViewFixedHeight extends ListView {
    public ListViewFixedHeight(Context context) {
        super(context);
    }

    public ListViewFixedHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewFixedHeight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
