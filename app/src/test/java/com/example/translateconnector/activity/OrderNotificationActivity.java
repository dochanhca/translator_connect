package com.example.translateconnector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.imoktranslator.R;
import com.imoktranslator.adapter.NotificationAdapter;
import com.imoktranslator.customview.EndlessRecyclerViewScrollListener;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.model.OrderNotificationModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.presenter.OrderNotificationPresenter;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Validator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OrderNotificationActivity extends BaseActivity implements
        OrderNotificationPresenter.OrderNotificationView,
        NotificationAdapter.OnOrderNotificationClickListener, HeaderView.BackButtonClickListener {

    @BindView(R.id.header_order_notifications)
    HeaderView headerView;
    @BindView(R.id.rv_notifications)
    RecyclerView rvNotifications;

    private OrderNotificationPresenter presenter;
    private NotificationAdapter adapter;
    private List<OrderNotificationModel> notificationList = new ArrayList<>();
    private OrderNotificationModel notificationToDelete;
    private LinearLayoutManager linearLayoutManager;
    private int currentPage = 1;
    private boolean needRestoreState = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_notifications;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new OrderNotificationPresenter(this, this);

        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        rvNotifications.setLayoutManager(linearLayoutManager);
        adapter = new NotificationAdapter(this, notificationList, this);
        rvNotifications.setAdapter(adapter);
        rvNotifications.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage++;
                presenter.getNotifications(currentPage, 10);
            }
        });

        presenter.markViewAllNotification();
        presenter.getNotifications(currentPage, 10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRestoreState) {
            doRestoreState();
        }
    }

    @Override
    public void getNotificationSuccessful(List<OrderNotificationModel> notificationList) {
        ArrayList<OrderNotificationModel> result = validateNotifications(notificationList);
        if (needRestoreState) {
            needRestoreState = false;
            adapter.clearAll();
            adapter.addAll(result);
            Log.d("Pagination", "Restore state done, page = " + currentPage + ", size = " + this.notificationList.size());
        } else {
            adapter.addAll(result);
            Log.d("Pagination", "page = " + currentPage + ", size: " + this.notificationList.size());
        }
    }

    private ArrayList<OrderNotificationModel> validateNotifications(List<OrderNotificationModel> notificationList) {
        ArrayList<OrderNotificationModel> list = new ArrayList<>();
        for (OrderNotificationModel notification : notificationList) {
            if (Validator.validNotificationInfo(notification)) {
                list.add(notification);
            }
        }
        return list;
    }

    @Override
    public void deleteNotificationSuccessful() {
        notificationList.remove(notificationToDelete);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void blockOrUnblockSuccessful() {
        if (needRestoreState) {
            doRestoreState();
        }
    }

    private void doRestoreState() {
        Log.d("Pagination", "Restoring...");
        presenter.getNotifications(1, this.notificationList.size());
    }

    @Override
    public void onNotificationClicked(OrderNotificationModel notification) {
        OrderDetailActivity.startActivity(this, notification.getOrder(),
                notification.getType() == OrderNotificationModel.NEW_MESSAGE
                        ? getReceiverId(notification) : -1);
        presenter.markAsRead(notification);
        needRestoreState = true;
    }

    private int getReceiverId(OrderNotificationModel notification) {
        PersonalInfo personalInfo = LocalSharedPreferences.getInstance(this).getPersonalInfo();
        return personalInfo.getId() == notification.getSenderId() ? notification.getReceiverId()
                : notification.getSenderId();
    }

    @Override
    public void onDeleteNotificationClicked(OrderNotificationModel notification) {
        notificationToDelete = notification;
        presenter.deleteNotification(notification.getId());
    }

    @Override
    public void onBlockNotificationClicked(OrderNotificationModel notification) {
        needRestoreState = true;
        if (notification.isBlocked(String.valueOf(notification.getSenderId()))) {
            presenter.unblock(notification);
        } else {
            presenter.block(notification);
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }
}
