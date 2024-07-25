package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.network.param.ForgotPasswordParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.ForgotPasswordResponse;
import com.imoktranslator.utils.DialogUtils;

/**
 * Created by ton on 3/30/18.
 */

public class ForgotPasswordPresenter extends BasePresenter {
    private ForgotPasswordView view;

    public ForgotPasswordPresenter(Context context, ForgotPasswordView view) {
        super(context);
        this.view = view;
    }

    public void requestSettingNewPassword(String email, String phone) {
        DialogUtils.showProgress(getContext());

        ForgotPasswordParam forgotPasswordParam = new ForgotPasswordParam(email, phone);
        requestAPI(getAPI().forgotPassword(forgotPasswordParam), new BaseRequest<ForgotPasswordResponse>() {
            @Override
            public void onSuccess(ForgotPasswordResponse response) {
                DialogUtils.hideProgress();
                view.openSecurityCodeScreen(response.getUserId(), email);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public interface ForgotPasswordView extends BaseView {

        void openSecurityCodeScreen(int userId, String userEmail);
    }
}
