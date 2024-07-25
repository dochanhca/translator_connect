package com.example.translateconnector.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;
import com.imoktranslator.customview.EndlessRecyclerViewScrollListener;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.dialog.ReasonCancelOrderDialog;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.UserNeedReview;
import com.imoktranslator.presenter.OrderManagementPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.OrderSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderManagementActivity extends BaseActivity implements OrderAdapter.OnOrderClickListener,
        OrderManagementPresenter.OrderManagementView, HeaderView.BackButtonClickListener {

    private static final String ORDER_STATUS = "ORDER_TYPE";

    public static final int ALL_ORDER = 0;
    public static final int ORDERED = 1;
    public static final int CANCELED = 2;
    public static final int SUCCESSFUL = 3;
    public static final int UPDATED_PRICE = 4;

    @BindView(R.id.layout_sort_order)
    LinearLayout layoutSortOrder;
    @BindView(R.id.rcv_order)
    RecyclerView rcvOrder;
    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.layout_sort_by_price)
    LinearLayout layoutSortByPrice;
    @BindView(R.id.layout_sort_by_time)
    LinearLayout layoutSortByTime;
    @BindView(R.id.layout_sort_by_quality)
    LinearLayout layoutSortByQuality;
    @BindView(R.id.img_sort_price)
    ImageView imgSortPrice;
    @BindView(R.id.img_sort_time)
    ImageView imgSortTime;
    @BindView(R.id.img_sort_quality)
    ImageView imgSortQuality;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private OrderManagementPresenter presenter;
    private OrderAdapter orderAdapter;
    private PersonalInfo personalInfo;

    private SORT_BY sortBy;

    private int status;

    private int updatedPosition;
    private EndlessRecyclerViewScrollListener onNotificationListScrollListener;

    private SwipeRefreshLayout.OnRefreshListener onRefreshData = () -> {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getListOrders(status, sortBy.getValue(), sortBy.getType(), false);
        onNotificationListScrollListener.resetState();
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_management;
    }

    @Override
    protected void initViews() {
        presenter = new OrderManagementPresenter(this, this);

        status = getIntent().getIntExtra(ORDER_STATUS, ALL_ORDER);
        sortBy = SORT_BY.SORT_PRICE_DESC;

        personalInfo = LocalSharedPreferences.getInstance(this).getPersonalInfo();
        initOrderList();

        header.setCallback(this);
        header.setTittle(getHeaderTitle());
        swipeRefreshLayout.setOnRefreshListener(onRefreshData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOrder();
    }

    private String getHeaderTitle() {
        switch (status) {
            case ALL_ORDER:
                return getString(R.string.MH21_003);
            case ORDERED:
                return getString(R.string.MH42_001);
            case CANCELED:
                return getString(R.string.MH43_001);
            case SUCCESSFUL:
                return getString(R.string.MH13_003);
            case UPDATED_PRICE:
                return getString(R.string.MH13_002);
                default:
                    return getString(R.string.MH21_003);
        }
    }

    private void getListOrder() {
        presenter.getListOrders(status, sortBy.getValue(), sortBy.getType(), false);
        onNotificationListScrollListener.resetState();
    }

    private void initOrderList() {
        orderAdapter = new OrderAdapter(getApplicationContext(), new ArrayList<>(), personalInfo);
        orderAdapter.setOnOrderClickListener(this);
        rcvOrder.setAdapter(orderAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(rcvOrder.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_list_divider));
        rcvOrder.addItemDecoration(divider);

        onNotificationListScrollListener = new EndlessRecyclerViewScrollListener(
                (LinearLayoutManager) rcvOrder.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.getListOrders(status, sortBy.getValue(), sortBy.getType(), true);
            }
        };
        rcvOrder.addOnScrollListener(onNotificationListScrollListener);
    }

    @OnClick({R.id.layout_sort_by_price, R.id.layout_sort_by_time, R.id.layout_sort_by_quality})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_sort_by_price:
                sortByPrice();
                break;
            case R.id.layout_sort_by_time:
                sortByTime();
                break;
            case R.id.layout_sort_by_quality:
                sortByQuality();
                break;
        }
    }

    private void sortByPrice() {
        sortBy = sortBy == SORT_BY.SORT_PRICE_DESC ? SORT_BY.SORT_PRICE_ASC : SORT_BY.SORT_PRICE_DESC;
        imgSortPrice.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();
        getListOrder();
    }

    private void sortByTime() {
        sortBy = sortBy == SORT_BY.SORT_DATE_DESC ? SORT_BY.SORT_DATE_ASC : SORT_BY.SORT_DATE_DESC;
        imgSortTime.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();

        getListOrder();
    }


    private void sortByQuality() {
        sortBy = sortBy == SORT_BY.SORT_SCORE_DESC ? SORT_BY.SORT_SCORE_ASC : SORT_BY.SORT_SCORE_DESC;
        imgSortQuality.setImageResource(sortBy.getType().equals(Constants.SORT_ASC) ? R.drawable.ic_sort_asc
                : R.drawable.ic_sort_desc);
        updateSortBtnBg();

        getListOrder();
    }

    private void updateSortBtnBg() {
        switch (sortBy) {
            case SORT_PRICE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_selected);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_normal);
                break;
            case SORT_DATE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_selected);
                break;
            case SORT_SCORE_DESC:
                layoutSortByPrice.setBackgroundResource(R.drawable.bg_sort_normal);
                layoutSortByQuality.setBackgroundResource(R.drawable.bg_sort_selected);
                layoutSortByTime.setBackgroundResource(R.drawable.bg_sort_normal);
                break;
        }
    }

    @Override
    public void onOrderItemClick(int position) {
        OrderModel orderModel = orderAdapter.getData().get(position);
        OrderDetailActivity.startActivity(this, orderModel, -1);
    }

    @Override
    public void onOrderCancelClick(int position) {
        showNotifyDialog(getString(R.string.MH22_025), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                  showDialogReasonCancel(position);
            }
        });

    }

    private void showDialogReasonCancel(int position) {
        ReasonCancelOrderDialog.showDialog(getSupportFragmentManager(), reason -> {
            updatedPosition = position;
            int orderId = orderAdapter.getData().get(position).getOrderId();
            presenter.updateOrderStatus(orderId, OrderModel.ORDER_STATUS.CANCELED_ORDER, reason);
        });
    }

    @Override
    public void onOrderDeleteClick(int position) {
        showNotifyDialog(getString(R.string.MH22_029), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                updatedPosition = position;
                int orderId = orderAdapter.getData().get(position).getOrderId();
                presenter.deleteOrder(orderId);
            }
        });
    }

    @Override
    public void onOrderEndEarlyClick(int position) {
        showNotifyDialog(getString(R.string.MH22_027), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                updatedPosition = position;
                int orderId = orderAdapter.getData().get(position).getOrderId();
                presenter.updateOrderStatus(orderId, OrderModel.ORDER_STATUS.FINISHED_ORDER);
            }
        });

    }

    @Override
    public void onHideOrderClick(int position) {
        showNotifyDialog(getString(R.string.MH22_029), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                OrderSharedPreferences.getInstance(OrderManagementActivity.this).saveHidingOrder(personalInfo.getId(),
                        orderAdapter.getData().get(position).getOrderId());
                orderAdapter.removeItem(position);
            }
        });

    }

    @Override
    public void onGetOrders(List<OrderModel> orderModels) {
        orderAdapter.clearData();
        orderAdapter.setData(orderModels);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetOrdersError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetMoreOrders(List<OrderModel> orderModels) {
        orderAdapter.setData(orderModels);
    }

    @Override
    public void onUpdateOrderStatus(OrderModel orderModel) {
        orderAdapter.getData().set(updatedPosition, orderModel);
        orderAdapter.notifyDataSetChanged();
        if (orderModel.getOrderStatus() == OrderModel.ORDER_STATUS.FINISHED_ORDER) {
            // show translator review activity
            Intent intent = new Intent(OrderManagementActivity.this, VotePartnerActivity.class);
            UserNeedReview userNeedReview = new UserNeedReview();
            userNeedReview.setOrderId(orderModel.getOrderId());
            userNeedReview.setUserId(orderModel.getUserId());
            userNeedReview.setAcceptedTranslatorId(orderModel.getAcceptedTransId());
            intent.putExtra(VotePartnerActivity.USER_NEED_REVIEW, userNeedReview);
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteOrder() {
        orderAdapter.removeItem(updatedPosition);
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    public enum SORT_BY {
        SORT_PRICE_DESC("price", "desc"),
        SORT_PRICE_ASC("price", "asc"),
        SORT_SCORE_DESC("score", "desc"),
        SORT_SCORE_ASC("score", "asc"),
        SORT_DATE_DESC("date", "desc"),
        SORT_DATE_ASC("date", "asc");

        private final String value;
        private final String type;

        SORT_BY(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    public static void startActivity(BaseActivity activity, int status) {
        Intent intent = new Intent(activity, OrderManagementActivity.class);
        intent.putExtra(ORDER_STATUS, status);
        activity.startActivity(intent);
    }
}
