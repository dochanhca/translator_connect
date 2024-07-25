package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.IntroduceResponse;

/**
 * Created by tontn on 3/25/18.
 */

public class SplashPresenter extends BasePresenter {
    private SplashView view;

    public SplashPresenter(Context context, SplashView view) {
        super(context);
        this.view = view;
    }

    public void fetchIntroData() {
        requestAPI(getAPI().fetchIntroduceData(), new BaseRequest<IntroduceResponse>() {
            @Override
            public void onSuccess(IntroduceResponse response) {
                view.openIntroduceScreen(response);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface SplashView extends BaseView {
        void openIntroduceScreen(IntroduceResponse introduceResponse);
    }
}
