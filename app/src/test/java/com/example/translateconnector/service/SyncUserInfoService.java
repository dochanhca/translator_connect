package com.example.translateconnector.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.imoktranslator.firebase.data.FirebaseUserData;

public class SyncUserInfoService extends IntentService {

    public SyncUserInfoService() {
        super(SyncUserInfoService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("Sync", "start service!");
        FirebaseUserData.getInstance().checkAndSyncUserInfo(this);
    }
}
