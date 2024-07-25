package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.activity.OrderManagementActivity;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.OrderInfoResponse;
import com.imoktranslator.network.response.OrderListResponse;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.OrderSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class OrderManagementPresenter extends BasePresenter {

    private OrderManagementView view;

    public OrderManagementPresenter(Context context, OrderManagementView view) {
        super(context);
        this.view = view;
    }

    public void getListOrders(int status, String sortBy, String type, boolean isLoadMore) {
        if (!isLoadMore) {
            PAGE_NUM = 0;
            DialogUtils.showProgress(getContext());
        }

        PAGE_NUM++;
        Call<OrderListResponse> getListOrder;
        if (status == 0) {
            getListOrder = getAPI().getOrders(sortBy, type, PAGE_NUM, PAGE_SIZE);
        } else {
            getListOrder = getListOrderAPI(status, sortBy, type);
        }

        requestAPI(getListOrder, new BaseRequest<OrderListResponse>() {
            @Override
            public void onSuccess(OrderListResponse response) {
                removeHidingOrder(response, isLoadMore);
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                if (!isLoadMore) {
                    DialogUtils.hideProgress();
                    showNetworkError(view, errCode, errMessage);
                    view.onGetOrdersError();
                }
            }
        });
    }

    private Call<OrderListResponse> getListOrderAPI(int status, String sortBy, String type) {
        Call<OrderListResponse> getListOrder;
        List<Integer> statuses = new ArrayList<>();
        switch (status) {
            case OrderManagementActivity.ORDERED:
                statuses.add(1);
                statuses.add(2);
                statuses.add(3);
                statuses.add(4);
                getListOrder = getAPI().getOrders(statuses, sortBy, type, PAGE_NUM, PAGE_SIZE);
                break;
            case OrderManagementActivity.CANCELED:
                statuses.add(3);
                getListOrder = getAPI().getOrders(statuses, sortBy, type, PAGE_NUM, PAGE_SIZE);
                break;
            case OrderManagementActivity.UPDATED_PRICE:
                statuses.add(1);
                statuses.add(2);
                statuses.add(3);
                statuses.add(4);
                getListOrder = getAPI().getOrders(statuses, sortBy, type, "accept",
                        PAGE_NUM, PAGE_SIZE);
                break;
            case OrderManagementActivity.SUCCESSFUL:
                statuses.add(2);
                statuses.add(4);
                getListOrder = getAPI().getOrders(statuses, sortBy, type, "accept",
                        PAGE_NUM, PAGE_SIZE);
                break;
            default:
                getListOrder = getAPI().getOrders(statuses, sortBy, type, PAGE_NUM, PAGE_SIZE);
                break;
        }
        return getListOrder;
    }

    private void removeHidingOrder(OrderListResponse response, boolean isLoadMore) {
        int userId = LocalSharedPreferences.getInstance(getContext()).getPersonalInfo().getId();
        List<OrderModel> orderModels = response.getOrderModels();
        List<Integer> orderIds = OrderSharedPreferences.getInstance(getContext()).getHidingOrders(userId);

        for (int hidingId : orderIds) {
            for (Iterator<OrderModel> iter = orderModels.listIterator(); iter.hasNext(); ) {
                OrderModel item = iter.next();
                if (item.getOrderId() == hidingId) {
                    iter.remove();
                    break;
                }
            }
        }

        if (isLoadMore) {
            view.onGetMoreOrders(orderModels);
        } else {
            view.onGetOrders(orderModels);
        }
    }

    public void updateOrderStatus(int id, int status) {
        updateOrderStatus(id, status, null);
    }

    public void updateOrderStatus(int id, int status, String reason) {
        Map<String, String> updateOrderPrams = new HashMap<>();
        updateOrderPrams.put("status", String.valueOf(status));
        if (reason != null) {
            updateOrderPrams.put("reason_to_cancel", reason);
        }

        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().updateOrderStatus(id, updateOrderPrams), new BaseRequest<OrderInfoResponse>() {
            @Override
            public void onSuccess(OrderInfoResponse response) {
                view.onUpdateOrderStatus(response.getOrderModel());
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void deleteOrder(int id) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().deleteOrder(id), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.onDeleteOrder();
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }


    public interface OrderManagementView extends BaseView {
        void onGetOrders(List<OrderModel> orderModels);

        void onGetOrdersError();

        void onGetMoreOrders(List<OrderModel> orderModels);

        void onUpdateOrderStatus(OrderModel orderModel);

        void onDeleteOrder();
    }
}
