package com.example.translateconnector.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imoktranslator.model.NearBySettingItem;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducpv on 3/24/18.
 */

public class LocalSharedPreferences {

    private static LocalSharedPreferences sInstance;

    private Gson gson = new Gson();
    private SharedPreferences mPreferences;

    private Context mContext;

    private LocalSharedPreferences(Context context) {
        this.mContext = context.getApplicationContext();
        if (mContext == null) {
            throw new NullPointerException("context is null");
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext
                .getApplicationContext());
    }

    public static LocalSharedPreferences getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocalSharedPreferences(context);
        }
        return sInstance;
    }

    public String getStringData(String key) {
        String data = mPreferences.getString(key, "");
        return data;
    }

    public void saveStringData(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value).commit();
    }

    public boolean getBooleanData(String key) {
        boolean data = mPreferences.getBoolean(key, false);
        return data;
    }

    public void saveBooleanData(String key, boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value).commit();
    }

    public void savePersonalInfo(PersonalInfo personalInfo) {
        String jsonString = personalInfo != null ? gson.toJson(personalInfo) : "";
        saveStringData(Constants.KEY_USER_INFO_DATA, jsonString);
    }

    public PersonalInfo getPersonalInfo() {
        String jsonString = getStringData(Constants.KEY_USER_INFO_DATA);
        return gson.fromJson(jsonString, PersonalInfo.class);
    }

    public void saveCachePersonalInfo(PersonalInfo personalInfo) {
        String jsonString = personalInfo != null ? gson.toJson(personalInfo) : "";
        saveStringData(Constants.KEY_USER_INFO_CACHED, jsonString);
    }

    public PersonalInfo getCachedPersonalInfo() {
        String jsonString = getStringData(Constants.KEY_USER_INFO_CACHED);
        return jsonString.isEmpty() ? null : gson.fromJson(jsonString, PersonalInfo.class);
    }

    public void saveCacheOrder(OrderModel orderModel) {
        String jsonString = orderModel != null ? gson.toJson(orderModel) : "";
        saveStringData(Constants.KEY_ORDER_CACHED, jsonString);
    }

    public OrderModel getCachedOrder() {
        String jsonString = getStringData(Constants.KEY_ORDER_CACHED);
        return gson.fromJson(jsonString, OrderModel.class);
    }

    public void clearAll() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear().apply();
    }

    public void removeData(String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void saveAccessToken(String accessToken) {
        saveStringData(Constants.KEY_ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return getStringData(Constants.KEY_ACCESS_TOKEN);
    }

    public void saveKeyUser(String key) {
        saveStringData(Constants.KEY_FIREBASE_USER, key);
    }

    public String getKeyUser() {
        return getStringData(Constants.KEY_FIREBASE_USER);
    }

    public void saveCurrentFirebaseUser(User user) {
        String jsonString = user != null ? gson.toJson(user) : "";
        saveStringData(Constants.KEY_FIREBASE_USER_INFO, jsonString);
    }

    public User getCurrentFirebaseUser() {
        String jsonString = getStringData(Constants.KEY_FIREBASE_USER_INFO);
        return gson.fromJson(jsonString, User.class);
    }

    public void saveNearBySetting(NearBySettingItem nearBySettingItem) {
        String jsonString = nearBySettingItem != null ? gson.toJson(nearBySettingItem) : "";
        saveStringData(Constants.KEY_NEAR_BY_SETTING, jsonString);
    }

    public NearBySettingItem getNearBySetting() {
        String jsonString = getStringData(Constants.KEY_NEAR_BY_SETTING);
        return gson.fromJson(jsonString, NearBySettingItem.class);
    }

    public List<User> getSearchContacts() {
        String searchContactsJson = getStringData(Constants.SEARCH_CONTACTS);
        List<User> searchContacts = gson.fromJson(searchContactsJson, new TypeToken<List<User>>() {
        }.getType());
        return searchContacts == null ? new ArrayList<>() : searchContacts;
    }

    public void saveToSearchContacts(User user) {
        List<User> recentSearchContacts = getSearchContacts();
        boolean isExist = false;
        for (User contact : recentSearchContacts) {
            if (contact.getId() == user.getId()) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            recentSearchContacts.add(0, user);
            if (recentSearchContacts.size() > 5) {
                recentSearchContacts.remove(recentSearchContacts.size() - 1);
            }
        }

        String searchContactsJson = gson.toJson(recentSearchContacts);
        saveStringData(Constants.SEARCH_CONTACTS, searchContactsJson);
    }

    public List<String> getSearchUserSuggestions() {
        String searchUserSuggestionsJson = getStringData(Constants.SEARCH_USER_SUGGESTIONS);
        List<String> suggestions = gson.fromJson(searchUserSuggestionsJson, new TypeToken<List<String>>() {
        }.getType());
        return suggestions == null ? new ArrayList<>() : suggestions;
    }

    public void saveSearchUserSuggestionsToLocal(String text) {
        List<String> recentSearchUserSuggestion = getSearchUserSuggestions();
        boolean isExist = false;
        for (String item : recentSearchUserSuggestion) {
            if (text.equalsIgnoreCase(item)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            recentSearchUserSuggestion.add(0, text);
            if (recentSearchUserSuggestion.size() > 5) {
                recentSearchUserSuggestion.remove(recentSearchUserSuggestion.size() - 1);
            }
        }

        String suggestionJson = gson.toJson(recentSearchUserSuggestion);
        saveStringData(Constants.SEARCH_USER_SUGGESTIONS, suggestionJson);
    }
}
