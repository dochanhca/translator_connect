package com.example.translateconnector.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.imoktranslator.activity.BaseActivity;

public class HandleCacheDataService extends Service {

    private static final String TAG = "HandleCacheDataService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "App being killed");
        stopSelf();
    }

    public static void startService(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, HandleCacheDataService.class);
        baseActivity.startService(intent);
    }

    public static void stopService(BaseActivity baseActivity) {
        Intent intent = new Intent(baseActivity, HandleCacheDataService.class);
        baseActivity.stopService(intent);
    }
}
