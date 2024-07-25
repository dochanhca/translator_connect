package com.example.translateconnector.utils;

import android.content.Context;

import com.imoktranslator.R;

public class GenderUtils {
    //Gender
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int OTHERS = 3;
    public static final int NOT_SELECTED_YET = 0;

    public static int convertGenderFrom(Context context, String genderString) {
        if (genderString.equals(context.getString(R.string.MH10_013))) {
            return MALE;
        } else if (genderString.equals(context.getString(R.string.MH10_014))) {
            return FEMALE;
        } else if (genderString.equals(context.getString(R.string.MH11_007))) {
            return OTHERS;
        } else {
            return NOT_SELECTED_YET;
        }
    }
}
