package com.example.translateconnector.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SwipeViewButton extends RelativeLayout {
    public SwipeViewButton(Context context) {
        super(context);
    }

    public SwipeViewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeViewButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
