package com.example.translateconnector.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderSharedPreferences {

    private static OrderSharedPreferences sInstance;

    private Gson gson = new Gson();
    private SharedPreferences mPreferences;

    private Context mContext;

    private OrderSharedPreferences(Context context) {
        this.mContext = context.getApplicationContext();
        if (mContext == null) {
            throw new NullPointerException("context is null");
        }
        mPreferences = context.getSharedPreferences("ORDER_SHARED_PREFERENCES", Context.MODE_PRIVATE);
    }

    public static OrderSharedPreferences getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new OrderSharedPreferences(context);
        }
        return sInstance;
    }

    public void saveHidingOrder(int userId, int orderId) {
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        String orderIdGson = mPreferences.getString(String.valueOf(userId), "");

        List<Integer> orderIds = orderIdGson.equals("") ? new ArrayList<>() :
                gson.fromJson(orderIdGson, listType);

        orderIds.add(orderId);

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(String.valueOf(userId), gson.toJson(orderIds)).commit();
    }

    public List<Integer> getHidingOrders(int userId) {
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        String orderIdGson = mPreferences.getString(String.valueOf(userId), "");
        return orderIdGson.equals("") ? new ArrayList<>() : gson.fromJson(orderIdGson, listType);
    }
}
