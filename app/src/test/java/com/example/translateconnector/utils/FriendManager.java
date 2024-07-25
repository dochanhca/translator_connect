package com.example.translateconnector.utils;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.User;

import java.util.ArrayList;
import java.util.List;

public class FriendManager {

    private static FriendManager instance;
    private Context context;

    public FriendManager(Context context) {
        this.context = context;
    }

    public static FriendManager getInstance(Context context) {
        if (instance == null) {
            instance = new FriendManager(context);
        }
        return instance;
    }

    public void countMutualFriends(List<User> users, CountFriendListener listener) {
        //get Current User's friend
        String userKey = LocalSharedPreferences.getInstance(context).getKeyUser();

        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.USER_FRIEND_COLLECTION).child(userKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Friend> currentUserFriends = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Friend friend = dsp.getValue(Friend.class);
                    currentUserFriends.add(friend);
                }
                compareFriends(users, currentUserFriends, listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                List<SearchFriend> searchFriends = new ArrayList<>();
                for (User user : users) {
                    searchFriends.add(getSearchFriend(user, 0));
                }
                listener.onSearchFriends(searchFriends);
            }
        });
    }

    private void compareFriends(List<User> users, List<Friend> currentUserFriends,
                                CountFriendListener listener) {
        List<SearchFriend> searchFriends = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            int index = i;
            Query getFriendQuery = FireBaseDataUtils.getInstance().getFirebaseReference().
                    child(FireBaseDataUtils.USER_FRIEND_COLLECTION).child(user.getKey());
            getFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int mutualFriend = 0;
                    if (dataSnapshot.getValue() != null) {
                        for (Friend currentUserFriend : currentUserFriends) {
                            if (dataSnapshot.hasChild(currentUserFriend.getUserKey())) {
                                mutualFriend++;
                            }
                        }
                    }

                    searchFriends.add(getSearchFriend(user, mutualFriend));

                    if (index == users.size() - 1) {
                        listener.onSearchFriends(searchFriends);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    searchFriends.add(getSearchFriend(user, 0));
                    if (index == users.size() - 1) {
                        listener.onSearchFriends(searchFriends);
                    }
                }
            });
        }
    }

    private SearchFriend getSearchFriend(User user, int mutualFriend) {
        SearchFriend searchFriend = new SearchFriend(mutualFriend);
        searchFriend.setKey(user.getKey());
        searchFriend.setId(user.getId());
        searchFriend.setName(user.getName());
        searchFriend.setGender(user.getGender());
        searchFriend.setDob(user.getDob());
        searchFriend.setAvatar(user.getAvatar());
        searchFriend.setLatitude(user.getLatitude());
        searchFriend.setLongitude(user.getLongitude());
        return searchFriend;
    }

    public interface CountFriendListener {
        void onSearchFriends(List<SearchFriend> searchFriends);
    }
}
