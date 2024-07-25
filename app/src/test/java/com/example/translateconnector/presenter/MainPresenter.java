package com.example.translateconnector.presenter;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.GeneralInfoResponse;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

public class MainPresenter extends BasePresenter {

    private MainView view;

    public MainPresenter(Context context, MainView view) {
        super(context);
        this.view = view;
    }

    public void getGeneralInfo() {
        requestAPI(getAPI().getGeneralInfo(), new BaseRequest<GeneralInfoResponse>() {
            @Override
            public void onSuccess(GeneralInfoResponse response) {
                view.getGeneralInfo(response);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
            }
        });
    }

    public void acceptExpand(int orderId) {
        view.showProgress();
        requestAPI(getAPI().acceptExpand(orderId), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.hideProgress();
                view.expandTimeOfNextOrder();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void cancelExpand(int orderId) {
        view.showProgress();
        requestAPI(getAPI().cancelExpand(orderId), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                view.hideProgress();
                view.expandTimeOfNextOrder();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void countUnreadMessage() {
        DialogUtils.showProgress(getContext());
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                .child(LocalSharedPreferences.getInstance(getContext()).getKeyUser()).orderByChild("lastTimeActive");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    try {
                        UserRoom userRoom = dsp.getValue(UserRoom.class);
                        if (!userRoom.isDeleted() && userRoom.getLastTimeActive() > 0 &&
                                userRoom.isHasUnreadMessage()) {
                            count++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                view.onUnreadMessageCount(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onUnreadMessageCount(0);
            }
        });
    }

    public interface MainView extends BaseView {

        void getGeneralInfo(GeneralInfoResponse generalInfoResponse);

        void expandTimeOfNextOrder();

        void onUnreadMessageCount(int number);
    }
}
