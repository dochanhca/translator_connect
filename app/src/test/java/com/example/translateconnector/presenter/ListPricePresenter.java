package com.example.translateconnector.presenter;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.PriceModel;
import com.imoktranslator.model.firebase.OfferPrice;
import com.imoktranslator.network.APIConstant;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.ListPriceResponse;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;

import java.util.List;

public class ListPricePresenter extends BasePresenter {

    private ListPriceView view;

    public ListPricePresenter(Context context, ListPriceView view) {
        super(context);
        this.view = view;
    }

    public void getListPrice(int id, String sortBy, String type) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().getListPrice(id, sortBy, type), new BaseRequest<ListPriceResponse>() {
            @Override
            public void onSuccess(ListPriceResponse response) {
                view.onGetListPrices(response.getPriceModels());
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                showNetworkError(view, errCode, errMessage);
                view.onGetListPricesError();
                DialogUtils.hideProgress();
            }
        });
    }

    public void acceptPrice(PriceModel priceModel) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().acceptPrice(priceModel.getOrderId(), priceModel.getId()), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.onAcceptPrice(priceModel);
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void deletePrice(int orderId, int priceId) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().deletePrice(orderId, priceId), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.onDeletePrice();
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void startOrderChat(int receiverId, int orderId) {
        DialogUtils.showProgress(getContext());
        FireBaseDataUtils.getInstance().getOrderRoomById(orderId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    showNetworkError(view, APIConstant.CHAT_ROOM_NOT_FOUND, "Unknow Error");
                } else {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        OfferPrice offerPrice = dsp.getValue(OfferPrice.class);
                        if (offerPrice.getTransID() == receiverId) {
                            String roomKey = offerPrice.getRoomKey();
                            view.goChatScreen(roomKey, receiverId);
                            break;
                        }
                    }
                }
                DialogUtils.hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                DialogUtils.hideProgress();
            }
        });
    }

    public interface ListPriceView extends BaseView {
        void onGetListPrices(List<PriceModel> priceModels);

        void onGetListPricesError();

        void onAcceptPrice(PriceModel priceModel);

        void onDeletePrice();

        void goChatScreen(String roomKey, int receiverId);
    }
}
