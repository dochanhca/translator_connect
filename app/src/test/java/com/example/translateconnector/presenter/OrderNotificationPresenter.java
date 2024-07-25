package com.example.translateconnector.presenter;

import android.content.Context;
import android.util.Log;

import com.imoktranslator.model.NotificationStatus;
import com.imoktranslator.model.OrderNotificationModel;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.ListOrderNotificationResponse;

import java.util.List;

public class OrderNotificationPresenter extends BasePresenter {
    private OrderNotificationView view;
    private Context context;

    public OrderNotificationPresenter(Context context, OrderNotificationView view) {
        super(context);
        this.context = context;
        this.view = view;
    }

    public void deleteNotification(Integer notificationId) {
        view.showProgress();
        requestAPI(getAPI().deleteNotification(notificationId), new BaseRequest<Void>() {
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

    public void markAsRead(OrderNotificationModel notification) {
        if (notification.getStatus() == NotificationStatus.UN_READ) {
            requestAPI(getAPI().markAsRead(notification.getId()), new BaseRequest<Void>() {
                @Override
                public void onSuccess(Void response) {
                    Log.d("MarkAsRead", "success");
                }

                @Override
                public void onFailure(int errCode, String errMessage) {
                }
            });
        }
    }

    public void unblock(OrderNotificationModel notification) {
        view.showProgress();
        requestAPI(getAPI().unblockNotification(notification.getSenderId(), notification.getOrderId()), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.hideProgress();
                view.blockOrUnblockSuccessful();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void block(OrderNotificationModel notification) {
        view.showProgress();
        requestAPI(getAPI().blockNotification(notification.getSenderId(), notification.getOrderId()), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.hideProgress();
                view.blockOrUnblockSuccessful();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void getNotifications(int currentPage, int perPage) {
        view.showProgress();

        requestAPI(getAPI().getOrderNotifications(currentPage, perPage), new BaseRequest<ListOrderNotificationResponse>() {
            @Override
            public void onSuccess(ListOrderNotificationResponse response) {
                view.hideProgress();
                view.getNotificationSuccessful(response.getNotificationList());
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void markViewAllNotification() {
        requestAPI(getAPI().viewAllOrderNotification(), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {

            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public interface OrderNotificationView extends BaseView {

        void getNotificationSuccessful(List<OrderNotificationModel> notificationList);

        void deleteNotificationSuccessful();

        void blockOrUnblockSuccessful();
    }
}
