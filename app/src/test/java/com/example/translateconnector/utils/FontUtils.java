package com.example.translateconnector.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by ducpv on 12/12/17.
 */

public class FontUtils {

    private static FontUtils instance;

    private Typeface openSans;
    private Typeface openSansSemiBold;
    private Typeface openSanBold;

    public final static FontUtils getInstance() {
        if (instance == null) {
            instance = new FontUtils();
        }

        return instance;
    }

    public final void initFonts(Context context) {
        openSans = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        openSansSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        openSanBold = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
    }

    public Typeface getOpenSans() {
        return openSans;
    }

    public Typeface getOpenSansSemiBold() {
        return openSansSemiBold;
    }

    public Typeface getOpenSanBold() {
        return openSanBold;
    }
}
