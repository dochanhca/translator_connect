package com.example.translateconnector.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.UserFriend;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.FireBaseDataUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private String currentUserKey;
    private List<Friend> friendList;
    private List<User> userList;
    private List<UserFriend> userFriends;

    public ContactManager(String currentUserKey, List<Friend> friendList, List<User> userList,
                          List<UserFriend> userFriends) {
        this.currentUserKey = currentUserKey;
        this.friendList = friendList;
        this.userList = userList;
        this.userFriends = userFriends;
    }

    public void getFriends(OnGetDataListener listener) {
        friendList.clear();
        userList.clear();
        userFriends.clear();
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(currentUserKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Friend friend = dsp.getValue(Friend.class);
                    friendList.add(friend);
                }
                if (friendList.size() == 0) {
                    listener.onSuccess(new ArrayList<>());
                }

                getUserList(listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFirebaseError();
            }
        });
    }

    private void getUserList(OnGetDataListener listener) {
        for (Friend friend : friendList) {
            queryUserFrom(friend.getUserKey(), listener);
        }
    }

    private void queryUserFrom(String friendKey, OnGetDataListener listener) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USERS_COLLECTION)
                .child(friendKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userList.add(dataSnapshot.getValue(User.class));

                        if (userList.size() == friendList.size()) {
                            listener.onSuccess(getUserFriends());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFirebaseError();
                    }
                });
    }

    private List<UserFriend> getUserFriends() {
        for (int i = 0; i < userList.size(); i++) {
            userFriends.add(new UserFriend(userList.get(i), friendList.get(i)));
        }
        return userFriends;
    }


    public interface OnGetDataListener {
        void onSuccess(List<UserFriend> userFriends);

        void onFirebaseError();
    }
}
