package com.example.translateconnector.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.imoktranslator.utils.FontUtils;

/**
 * Created by ducpv on 3/24/18.
 */

public class OpenSansSemiBoldTextView extends AppCompatTextView {

    public OpenSansSemiBoldTextView(Context context) {
        super(context);
        initFont();
    }

    public OpenSansSemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public OpenSansSemiBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFont();
    }

    private void initFont() {
        Typeface font = FontUtils.getInstance().getOpenSansSemiBold();
        this.setTypeface(font);
    }
}
