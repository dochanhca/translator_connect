package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.NotificationStatus;
import com.imoktranslator.model.SocialNotificationModel;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.SocialNotificationResponse;
import com.imoktranslator.utils.DialogUtils;

import java.util.List;

public class SocialNotificationPresenter extends BasePresenter {
    private SocialNotificationView view;

    public SocialNotificationPresenter(Context context, SocialNotificationView view) {
        super(context);
        this.view = view;
    }

    public void getNotifications(boolean isLoadMore) {
        if (!isLoadMore) {
            PAGE_NUM = 0;
            DialogUtils.showProgress(getContext());
        }
        PAGE_NUM++;
        view.showProgress();

        requestAPI(getAPI().getSocialNotifications(PAGE_NUM, PAGE_SIZE), new BaseRequest<SocialNotificationResponse>() {
            @Override
            public void onSuccess(SocialNotificationResponse response) {
                view.hideProgress();
                view.getNotificationSuccessful(response.getNotificationList(), isLoadMore);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                view.getNotificationError();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void deleteNotification(Integer notificationId) {
        view.showProgress();
        requestAPI(getAPI().deleteSocialNotification(notificationId), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.hideProgress();
                view.deleteNotificationSuccessful();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void markAsRead(SocialNotificationModel notification, int position) {
        if (notification.getStatus() == NotificationStatus.UN_READ) {
            requestAPI(getAPI().socialNotificationMarkAsRead(notification.getId()), new BaseRequest<Void>() {
                @Override
                public void onSuccess(Void response) {
                    view.markAsReaded(notification, position);
                }

                @Override
                public void onFailure(int errCode, String errMessage) {
                }
            });
        }
    }

    public void markViewAllNotification() {
        requestAPI(getAPI().viewAllNotification(), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {

            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public interface SocialNotificationView extends BaseView {
        void getNotificationSuccessful(List<SocialNotificationModel> notificationList, boolean isLoadMore);

        void getNotificationError();

        void deleteNotificationSuccessful();

        void markAsReaded(SocialNotificationModel notification, int position);
    }
}
