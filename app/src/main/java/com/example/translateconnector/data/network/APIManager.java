package com.example.translateconnector.data.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ducpv on 3/24/18.
 */

public class APIManager {

    private static APIManager mInstance;
    public TranlookAPI tranlookAPI;
    private Context context;

    private static final int RETROFIT_TIMEOUT = 60 * 1000;

    private APIManager(Context context) {
        this.context = context;
        init();
    }

    public static APIManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new APIManager(context);
        }
        return mInstance;
    }

    private void init() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(APIConstant.BASE_URL);
        OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .headers(getJsonHeaders()).build();

                    return chain.proceed(request);
                })
                .addInterceptor(createHttpLogging())
                .addInterceptor(new ConnectivityInterceptor(context))
                .connectTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        builder.client(defaultHttpClient);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        builder.addConverterFactory(GsonConverterFactory
                .create(gson));
        Retrofit retrofit = builder.build();
        tranlookAPI = retrofit.create(TranlookAPI.class);
    }

    private Headers getJsonHeaders() {
        Headers.Builder header = new Headers.Builder();

        String accessToken = LocalSharedPreferences.getInstance(context).getAccessToken();
        if (!TextUtils.isEmpty(accessToken)) {
            header.add("Authorization", "Bearer " + accessToken);
        }
        header.add("Content-Type", "application/x-www-form-urlencoded")
                .add("client-id", "1")
                .add("client-secret", "IfeoFekeLPOi3Y4qRz84mrfu54rPPQwWDgBGUrub")
                .build();
        return header.build();
    }

    private HttpLoggingInterceptor createHttpLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d("Tranlook API: ", message));
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return logging;
    }

}
