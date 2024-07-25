package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.DialogUtils;

public class UserDetailPresenter extends BasePresenter {

    protected UserDetailView view;

    public UserDetailPresenter(Context context, UserDetailView view) {
        super(context);
        this.view = view;
    }

    public void getUserDetail(int id) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().fetchUserInfo(id), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                view.onGetUserDetail(response.getPersonalInfo());
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface UserDetailView extends BaseView {
        void onGetUserDetail(PersonalInfo personalInfo);
    }
}
