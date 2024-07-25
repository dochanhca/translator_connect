package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.exception.NetworkException;
import com.imoktranslator.network.APIManager;
import com.imoktranslator.network.TranlookAPI;
import com.imoktranslator.network.request.BaseRequest;

import retrofit2.Call;

/**
 * Created by ducpv on 3/24/18.
 */

public abstract class BasePresenter {

    protected static int PAGE_NUM = 0;
    protected static final int PAGE_SIZE = 20;
    protected static  String TAG;

    private Context context;
    private TranlookAPI tranlookAPI;

    public BasePresenter(Context context) {
        this.context = context;
        this.tranlookAPI = APIManager.getInstance(context).tranlookAPI;
        TAG = getClass().getSimpleName();
    }

    protected final TranlookAPI getAPI() {
        return this.tranlookAPI;
    }

    protected <T> void requestAPI(Call<T> call, BaseRequest<T> callBack) {
        call.enqueue(callBack);
    }

    protected final Context getContext() {
        return this.context;
    }

    protected void showNetworkError(BaseView baseView, int errCode, String errMessage) {
        NetworkException exceptionHelper = new NetworkException(getContext(), errCode, errMessage);
        baseView.notify(exceptionHelper.getMessage());
    }
}
