package com.example.translateconnector.network.request;

import com.imoktranslator.TranlookApplication;
import com.imoktranslator.exception.NoConnectivityException;
import com.imoktranslator.network.APIConstant;
import com.imoktranslator.network.response.APIError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ducpv on 3/24/18.
 */

public abstract class BaseRequest<U> implements Callback<U> {

    public abstract void onSuccess(U response);

    public abstract void onFailure(int errCode, String errMessage);


    @Override
    public void onResponse(Call<U> call, Response<U> response) {

        if (!response.isSuccessful()) {
            try {
                String error = response.errorBody().string();
                APIError apiError = TranlookApplication.getGson().fromJson(error, APIError.class);
                onFailure(apiError.getStatusCode(), apiError.getMessage());
            } catch (Exception e) {
                onFailure(response.code(), e.toString());
            }
        } else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(Call<U> call, Throwable t) {
        if (t instanceof NoConnectivityException || call.isCanceled()) {
            onFailure(APIConstant.NO_NETWORK, "Khong co ket noi internet");
        } else {
            onFailure(APIConstant.INTERNAL_SERVER_ERROR, t.getMessage());
        }
    }
}
