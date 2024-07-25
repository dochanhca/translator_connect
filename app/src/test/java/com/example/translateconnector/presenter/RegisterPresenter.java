package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.RegisterItem;
import com.imoktranslator.network.param.UserRegisterParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.RegisterResponse;
import com.imoktranslator.utils.DialogUtils;

/**
 * Created by ducpv on 3/26/18.
 */

public class RegisterPresenter extends BasePresenter {

    private RegisterView registerView;

    public RegisterPresenter(Context context, RegisterView view) {
        super(context);
        this.registerView = view;
    }

    public void doRegister(RegisterItem registerItem) {

        DialogUtils.showProgress(getContext());
        UserRegisterParam userRegisterParam = new UserRegisterParam();
        userRegisterParam.setEmail(registerItem.getEmail());
        userRegisterParam.setName(registerItem.getName());
        //userRegisterParam.setPassword(Utils.md5(registerItem.getPass()));
        userRegisterParam.setPassword(registerItem.getPass());
        userRegisterParam.setPhone(registerItem.getPhone());

        requestAPI(getAPI().register(userRegisterParam), new BaseRequest<RegisterResponse>() {
            @Override
            public void onSuccess(RegisterResponse response) {
                DialogUtils.hideProgress();
                if (response != null && response.getData() != null) {
                    int userId = response.getData().getId();
                    String phoneNumber = registerItem.getPhone();
                    String password = registerItem.getPass();
                    String email = registerItem.getEmail();
                    registerView.openVerifyAccountScreen(userId, phoneNumber, password, email);
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                showNetworkError(registerView, errCode, errMessage);
                DialogUtils.hideProgress();
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

    public interface RegisterView extends BaseView {
        void openVerifyAccountScreen(int userId, String phoneNumber, String password, String email);
    }
}
