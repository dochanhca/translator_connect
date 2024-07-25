package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FriendManager;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchFriendResultPresenter extends BasePresenter {

    private View view;
    private String userKey;
    private PersonalInfo personalInfo;

    public SearchFriendResultPresenter(Context context, View view) {
        super(context);
        this.view = view;
        userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void getFriendByCountryAndCity(String country, String city) {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("registerCity").equalTo(city).limitToFirst(40);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    view.onSearchFriendsFail();
                }
                List<User> users = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    if (user.getKey().equals(userKey) || user.isTranslator() != personalInfo.isTranslator()) {
                        continue;
                    }

                    if (TextUtils.isEmpty(country) || user.getRegisterCountry().equals(country)) {
                        users.add(user);
                    }
                }
                removeAddedFriends(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                view.onSearchFriendsFail();
                DialogUtils.hideProgress();
            }
        });
    }

    public void getFriendByPhoneNumber(String phoneNumber) {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("phone").equalTo(phoneNumber).limitToFirst(40);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    view.onSearchFriendsFail();
                    return;
                }
                List<User> users = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    if (!user.getKey().equals(userKey) && user.isTranslator() == personalInfo.isTranslator()) {
                        users.add(user);
                    }
                }
                removeAddedFriends(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                view.onSearchFriendsFail();
            }
        });
    }

    private void removeAddedFriends(List<User> users) {
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove added friend
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    for (Iterator<User> iter = users.listIterator(); iter.hasNext(); ) {
                        User item = iter.next();
                        if (item.getKey().equals(dsp.getKey())) {
                            iter.remove();
                            break;
                        }
                    }
                }

                if (users.size() > 0) {
                    calculateMutualFriends(users);
                } else {
                    view.onSearchFriendsFail();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (users.size() > 0) {
                    calculateMutualFriends(users);
                } else {
                    view.onSearchFriendsFail();

                }
            }
        });
    }

    private void calculateMutualFriends(List<User> users) {
        FriendManager.getInstance(getContext()).countMutualFriends(users, searchFriends ->
            view.onSearchFriends(searchFriends));
    }

    public void sendFriendInvitation(SearchFriend item) {
        Invitation invitation = new Invitation(userKey
                , personalInfo.getId(), item.getMutualFriend());
        FireBaseDataUtils.getInstance().sendInvitation(invitation, item.getKey(), (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        FireBaseDataUtils.getInstance().getFirebaseReference().
                                child(FireBaseDataUtils.USERS_COLLECTION).orderByChild("key").equalTo(item.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                                        sendNotify(user.getId());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }
        );
    }

    private void sendNotify(int receiverId) {
        Integer[] receiverIds = new Integer[1];
        receiverIds[0] = receiverId;

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_ADD_FRIEND));
        requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                Log.d(TAG, "Send notification success to: " + receiverId);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public interface View extends BaseView {
        void onSearchFriends(List<SearchFriend> searchFriends);

        void onSearchFriendsFail();
    }
}
