package com.example.translateconnector.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.imoktranslator.utils.FontUtils;

/**
 * Created by ducpv on 3/24/18.
 */

public class OpenSansTextView extends AppCompatTextView {

    public OpenSansTextView(Context context) {
        super(context);
        initFont();
    }

    public OpenSansTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public OpenSansTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFont();
    }

    private void initFont() {
        Typeface font = FontUtils.getInstance().getOpenSans();
        this.setTypeface(font);
    }
}
