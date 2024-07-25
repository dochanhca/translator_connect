package com.example.translateconnector.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.imoktranslator.TranlookApplication;
import com.imoktranslator.model.PhoneCodeItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by ducpv on 3/25/18.
 */

public class ChooseCountryPresenter extends BasePresenter {

    private ChooseCountryView view;

    public ChooseCountryPresenter(Context context, ChooseCountryView view) {
        super(context);
        this.view = view;
    }

    public void getPhoneCodesFromAsserts() {
        BufferedReader reader = null;
        StringBuilder fileValue = new StringBuilder();

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getContext().getAssets().open("phone_code_vi.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                fileValue.append(mLine);
                fileValue.append('\n');
            }

            List<PhoneCodeItem> phoneCodeItems = TranlookApplication.getGson()
                    .fromJson(fileValue.toString(), new TypeToken<List<PhoneCodeItem>>() {
                    }.getType());
            view.getPhoneCodes(phoneCodeItems);
        } catch (IOException e) {
            Log.e("Error reading file!", e.getMessage());
            view.getPhoneCodesError(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    public interface ChooseCountryView {
        void getPhoneCodes(List<PhoneCodeItem> phoneCodeItems);

        void getPhoneCodesError(String errMessage);
    }
}
