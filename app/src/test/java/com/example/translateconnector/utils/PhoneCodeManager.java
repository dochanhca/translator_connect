package com.example.translateconnector.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.imoktranslator.TranlookApplication;
import com.imoktranslator.model.PhoneCodeItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ton on 3/31/18.
 */

public class PhoneCodeManager {
    private static PhoneCodeManager instance;
    private static List<PhoneCodeItem> phoneCodeItemList;

    private PhoneCodeManager() {

    }

    public static PhoneCodeManager getInstance() {
        if (instance == null) {
            instance = new PhoneCodeManager();
        }
        return instance;
    }

    private List<PhoneCodeItem> getPhoneCodeList(Context context) {
        if (phoneCodeItemList == null || phoneCodeItemList.isEmpty()) {
            Log.d(PhoneCodeManager.class.getSimpleName(), "initial phone code list");
            BufferedReader reader = null;
            StringBuilder fileValue = new StringBuilder();

            try {
                reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("phone_code_vi.txt")));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    fileValue.append(mLine);
                    fileValue.append('\n');
                }

                phoneCodeItemList = TranlookApplication.getGson().fromJson(fileValue.toString(), new TypeToken<List<PhoneCodeItem>>() {
                }.getType());
            } catch (IOException e) {
                Log.e("Error reading file!", e.getMessage());
                phoneCodeItemList = new ArrayList<>();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        } else {
            Log.d(PhoneCodeManager.class.getSimpleName(), "no need initial list phone code");
        }
        return phoneCodeItemList;
    }

    public String getCountryFrom(Context context, String phone) {
        String countryCode = phone.split(" ")[0];
        List<PhoneCodeItem> phoneCodeItemList = getPhoneCodeList(context);
        for (PhoneCodeItem item : phoneCodeItemList) {
            if (countryCode.equals(item.getCode())) {
                return item.getCountry();
            }
        }
        return "";
    }
}
