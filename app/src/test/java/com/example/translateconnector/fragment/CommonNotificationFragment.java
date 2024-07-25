package com.example.translateconnector.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.imoktranslator.R;
import com.imoktranslator.activity.GeneralNotificationActivity;
import com.imoktranslator.activity.PostDetailActivity;
import com.imoktranslator.adapter.SocialNotificationAdapter;
import com.imoktranslator.customview.EndlessRecyclerViewScrollListener;
import com.imoktranslator.model.SocialNotificationModel;
import com.imoktranslator.presenter.SocialNotificationPresenter;
import com.imoktranslator.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommonNotificationFragment extends BaseFragment implements
        SocialNotificationPresenter.SocialNotificationView,
        SocialNotificationAdapter.OnSocialNotificationClickListener {

    @BindView(R.id.rv_notifications)
    RecyclerView rvNotifications;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SocialNotificationAdapter adapter;
    private SocialNotificationPresenter presenter;
    private SocialNotificationModel notificationToDelete;

    private EndlessRecyclerViewScrollListener onNotificationListScrollListener;


    public static CommonNotificationFragment newInstance() {
        return new CommonNotificationFragment();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getNotifications(false);
        onNotificationListScrollListener.resetState();
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_notification_fragment;
    }

    @Override
    protected void initViews() {
        adapter = new SocialNotificationAdapter(getContext(), new ArrayList<>(), this);
        rvNotifications.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(onRefreshData);

        onNotificationListScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager)
                rvNotifications.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.getNotifications(true);
            }
        };
        rvNotifications.addOnScrollListener(onNotificationListScrollListener);

        presenter = new SocialNotificationPresenter(getContext(), this);
        presenter.getNotifications(false);
        presenter.markViewAllNotification();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void getNotificationSuccessful(List<SocialNotificationModel> notificationList,
                                          boolean isLoadMore) {
        if(!isLoadMore) {
            adapter.clearData();
        }
        adapter.addAll(notificationList);
        swipeRefreshLayout.setRefreshing(false);
        getNotificationActivity().setNotificationCount(adapter.getItemCount());
    }

    @Override
    public void getNotificationError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void deleteNotificationSuccessful() {
        adapter.remove(notificationToDelete);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void markAsReaded(SocialNotificationModel notification, int position) {
        notification.setStatus(1);
        adapter.itemChange(notification, position);
    }

    @Override
    public void onNotificationClicked(SocialNotificationModel notification, int position) {
        presenter.markAsRead(notification, position);
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(Constants.POST_ID_KEY, notification.getModelId());
        startActivity(intent);
    }

    @Override
    public void onDeleteNotification(SocialNotificationModel notification) {
        notificationToDelete = notification;
        presenter.deleteNotification(notification.getId());
    }

    public GeneralNotificationActivity getNotificationActivity() {
        return (GeneralNotificationActivity) getBaseActivity();
    }
}
