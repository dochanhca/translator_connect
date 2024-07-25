package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.NotificationSetting;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.NotificationSettingResponse;

public class NotificationSettingPresenter extends BasePresenter {

    private NotificationSettingView view;

    public NotificationSettingPresenter(Context context, NotificationSettingView view) {
        super(context);
        this.view = view;
    }

    public void getNotificationSetting() {
        view.showProgress();
        requestAPI(getAPI().getNotificationSetting(), new BaseRequest<NotificationSettingResponse>() {
            @Override
            public void onSuccess(NotificationSettingResponse response) {
                view.hideProgress();
                view.onGetNotificationSetting(response.getNotificationSetting());
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void settingNotification(NotificationSetting notificationSetting) {
        view.showProgress();
        requestAPI(getAPI().settingNotification(notificationSetting), new BaseRequest<NotificationSettingResponse>() {
            @Override
            public void onSuccess(NotificationSettingResponse response) {
                view.hideProgress();
                view.onSettingNotification();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface NotificationSettingView extends BaseView {
        void onGetNotificationSetting(NotificationSetting notificationSetting);

        void onSettingNotification();
    }
}
