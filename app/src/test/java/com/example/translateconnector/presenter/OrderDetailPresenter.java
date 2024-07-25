package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.UpdatePriceParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.UpdatePriceResponse;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;

public class OrderDetailPresenter extends BasePresenter {

    private OrderDetailView view;

    public OrderDetailPresenter(Context context, OrderDetailView view) {
        super(context);
        this.view = view;
    }

    public void updatePrice(OrderModel orderModel, boolean isFirstTime, PersonalInfo personalInfo) {
        DialogUtils.showProgress(getContext());
        UpdatePriceParam updatePriceParam = new UpdatePriceParam(orderModel.getPrice(),
                orderModel.getCurrency());

        requestAPI(getAPI().updateOrderPrice(orderModel.getOrderId(), updatePriceParam), new BaseRequest<UpdatePriceResponse>() {
            @Override
            public void onSuccess(UpdatePriceResponse response) {
                view.onUpdatePrice(response.getUpdatePriceParam());
                if (isFirstTime) {
                    FireBaseDataUtils.getInstance().addNewOffer(getContext(),
                            orderModel.getOrderId(), personalInfo.getId());
                }
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface OrderDetailView extends BaseView {
        void onUpdatePrice(UpdatePriceParam response);
    }
}
