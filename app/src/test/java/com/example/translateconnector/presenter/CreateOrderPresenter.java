package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.OrderInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

public class CreateOrderPresenter extends BasePresenter {

    private CreateOrderView createOrderView;
    private PersonalInfo personalInfo;

    public CreateOrderPresenter(Context context, CreateOrderView view) {
        super(context);
        this.createOrderView = view;
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void createOrder(OrderModel orderModel) {
        DialogUtils.showProgress(getContext());

        orderModel.setOrderStatus(OrderModel.ORDER_STATUS.SEARCHING_ORDER);
        orderModel.setUserId(personalInfo.getId());
        requestAPI(getAPI().createOrder(orderModel),
                new BaseRequest<OrderInfoResponse>() {
                    @Override
                    public void onSuccess(OrderInfoResponse response) {
                        LocalSharedPreferences.getInstance(getContext()).removeData(Constants.KEY_ORDER_CACHED);
                        createOrderView.onCreateOrderSuccess();
                        DialogUtils.hideProgress();
                    }

                    @Override
                    public void onFailure(int errCode, String errMessage) {
                        showNetworkError(createOrderView, errCode, errMessage);
                        DialogUtils.hideProgress();
                    }
                });
    }


    public interface CreateOrderView extends BaseView {
        void onCreateOrderSuccess();
    }
}
