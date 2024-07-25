package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Friend;
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
import java.util.List;
import java.util.Map;

public class SearchUserResultPresenter extends BasePresenter {
    private final String userKey;
    private final PersonalInfo personalInfo;
    private SearchUserResultView view;

    public SearchUserResultPresenter(Context context, SearchUserResultView view) {
        super(context);
        this.view = view;
        userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void searchUserByName(String textSearch) {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> filteredUsers = new ArrayList<>();

                User user;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    user = dsp.getValue(User.class);
                    if (user != null
                            && user.getName().toLowerCase().contains(textSearch.toLowerCase())
                            && !user.getKey().equals(userKey)
                            && user.isTranslator() == personalInfo.isTranslator()) {
                        filteredUsers.add(user);
                    }
                }

                if (filteredUsers.isEmpty()) {
                    view.onSearchUsersFail();
                } else {
                    calculateMutualFriends(filteredUsers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                view.onSearchUsersFail();
            }
        });
    }

    private void calculateMutualFriends(List<User> users) {
        FriendManager.getInstance(getContext()).countMutualFriends(users, searchFriends -> {
            view.onSearchFriends(searchFriends);
        });
    }

    public void searchMyFriendKeys() {
        view.showProgress();
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> friendKeys = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    friendKeys.add(dsp.getKey());
                }

                view.searchMyFriendKeysDone(friendKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onSearchUsersFail();
            }
        });
    }

    public void sendFriendInvitation(SearchFriend searchFriend) {
        Invitation invitation = new Invitation(userKey
                , personalInfo.getId(), searchFriend.getMutualFriend());
        FireBaseDataUtils.getInstance().sendInvitation(invitation, searchFriend.getKey(), (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        FireBaseDataUtils.getInstance().getFirebaseReference().
                                child(FireBaseDataUtils.USERS_COLLECTION).orderByChild("key").equalTo(searchFriend.getKey())
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

    public interface SearchUserResultView extends BaseView {

        void onSearchUsersFail();

        void onSearchFriends(List<SearchFriend> searchFriends);

        void searchMyFriendKeysDone(List<String> friendKeys);
    }
}
