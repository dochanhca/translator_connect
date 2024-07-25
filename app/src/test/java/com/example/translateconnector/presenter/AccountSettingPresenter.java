package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.TranlookApplication;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class AccountSettingPresenter extends BasePresenter {

    private AccountSettingView view;
    private String currentUserKey;
    private PostManager postManager;

    public AccountSettingPresenter(Context context, AccountSettingView view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        postManager = new PostManager();
    }

    public void changeLanguage(String language) {
        view.showProgress();
        Map<String, String> updateStatus = new HashMap<>();
        updateStatus.put("setting_language", language);
        requestAPI(getAPI().updateProfileInfo(updateStatus), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                TranlookApplication.updateAppLanguage(getContext(), language);
                view.onSettingLanguage();
                view.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void changePostPriority(String priorityMode) {
        view.showProgress();
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .child(currentUserKey).child("prioritySetting")
                .setValue(priorityMode, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        User user = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();
                        user.setPrioritySetting(priorityMode);
                        LocalSharedPreferences.getInstance(getContext()).saveCurrentFirebaseUser(user);
                        view.onChangePriority(priorityMode);
                    } else {
                        view.notify(getContext().getString(R.string.TB_1053));
                    }
                    view.hideProgress();
                });
    }

    public void getPrioritySetting() {
        view.showProgress();
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("key").equalTo(currentUserKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = null;
                        try {
                            user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String prioritySetting =
                                (user == null || TextUtils.isEmpty(user.getPrioritySetting()))
                                        ? Constants.PUBLIC_MODE : user.getPrioritySetting();
                        view.onGetPrioritySetting(prioritySetting);
                        view.hideProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.hideProgress();
                        view.onGetPrioritySetting(Constants.PUBLIC_MODE);
                    }
                });
    }

    public interface AccountSettingView extends BaseView {
        void onSettingLanguage();

        void onChangePriority(String priority);

        void onGetPrioritySetting(String priority);
    }
}
