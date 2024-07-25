package com.example.translateconnector.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Layout;
import android.widget.TextView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ducpv on 3/29/18.
 */

public class Utils {
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static CharSequence numberFilter(CharSequence source, int start, int end) {
        if (source == null) {
            return null;
        }

        String result = "";
        for (int i = start; i < end; i++) {
            if (!Character.isDigit(source.charAt(i))) {
                result += "";
            } else {
                result += source.charAt(i);
            }
        }
        return result;
    }

    public static String formatCurrency(double value) {
        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat) nf2).applyPattern("####,###,###.00");
        String result = nf2.format(value);

        return result.replace(".00", "");
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static Address getCurrentAddress(Context context, double lat, double lon) throws IOException {

        Geocoder mGeocoder = new Geocoder(context, Locale.US);
        String zipcode = null;
        Address address = null;

        if (mGeocoder != null) {

            List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 5);

            address = addresses.get(0);
        }
        return address;
    }

    public static String hideStringWithStars(String in) {
        String out = "";
        for (int i = 0; i < in.length(); i++) {
            if (i == 0 || i == in.length() - 1) {
                out += in.charAt(i);
            } else {
                out += "*";
            }
        }

        return out;
    }

    public static String getAgeFromDob(String dob) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try {
            int age = year - Integer.valueOf(dob);
            return String.valueOf(age);
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    public static String getDistance(float distance) {
        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat) nf2).applyPattern("#0.0");
        String unit = distance < 1000 ? "m" : "km";
        String result;
        if (unit.equals("km")) {
            result = nf2.format(distance / 1000);
        } else {
            result = nf2.format(distance);
        }
        return result + unit;
    }

    public static  boolean isTextEllipsized(TextView textView) {
        Layout layout = textView.getLayout();
        if(layout != null) {
            int lines = layout.getLineCount();
            if(lines > 0) {
                int ellipsisCount = layout.getEllipsisCount(lines-1);
                if ( ellipsisCount > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
