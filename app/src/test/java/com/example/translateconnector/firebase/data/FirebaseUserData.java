package com.example.translateconnector.firebase.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.firebase.model.CommentStats;
import com.imoktranslator.firebase.model.PostStats;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.imoktranslator.utils.FireBaseDataUtils.AUTHOR_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.COMMENTS;
import static com.imoktranslator.utils.FireBaseDataUtils.NEWS_FEED;
import static com.imoktranslator.utils.FireBaseDataUtils.TIME_LINE;
import static com.imoktranslator.utils.FireBaseDataUtils.WALL;

public class FirebaseUserData {
    private static FirebaseUserData instance = null;

    private FirebaseUserData() {
    }

    public static FirebaseUserData getInstance() {
        if (instance == null) {
            instance = new FirebaseUserData();
        }
        return instance;
    }

    public void getCurrentFirebaseUser(String userKey, OnGetFirebaseUserDataListener listener) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USERS_COLLECTION)
                .child(userKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (listener != null) {
                            listener.onSuccess(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (listener != null) {
                            listener.onFail(databaseError);
                        }
                    }
                });
    }

    public void checkAndSyncUserInfo(Context context) {
        String userKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        if (TextUtils.isEmpty(userKey)) {
            return;
        }

        getCurrentFirebaseUser(userKey, new OnGetFirebaseUserDataListener() {
            @Override
            public void onSuccess(User user) {
                LocalSharedPreferences.getInstance(context).saveCurrentFirebaseUser(user);
                syncUserInfo(context, user);
            }

            @Override
            public void onFail(DatabaseError databaseError) {

            }
        });
    }

    private void syncUserInfo(Context context, User user) {
        if (context == null) return;

        Map<String, Object> childUpdates = new HashMap<>();
        String postId;

        //cap nhat thong tin user trong cac post
        Collection<PostStats> postStatsCollection = user.getPostStats().values();
        for (PostStats postStats : postStatsCollection) {
            postId = postStats.getPostId();
            if (!postStats.getViewer().isEmpty()) {
                updateMyNewsFeedAndWall(user, childUpdates, postId);
                updateMyFriendsNewsFeed(postStats, user, childUpdates, postId);
            } else {
                updateMyNewsFeedAndWall(user, childUpdates, postId);
            }
        }

        //cap nhat thong tin user trong cac comment
        Collection<CommentStats> commentStatsCollection = user.getCommentStats().values();
        for (CommentStats commentStats : commentStatsCollection) {
            postId = commentStats.getPostId();
            for (String commentId : commentStats.getCommentIds().keySet()) {
                childUpdates.put("/" + TIME_LINE + "/" + COMMENTS + "/" + postId + "/" + commentId + "/" + AUTHOR_CHILD_NODE, user);
            }
        }

        FireBaseDataUtils.getInstance().getFirebaseReference().updateChildren(childUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.e("Sync", "sync done!");
                });
    }

    private void updateMyFriendsNewsFeed(PostStats postStats, User author, Map<String, Object> childUpdates, String postId) {
        for (String friendKey : postStats.getViewer().keySet()) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId + "/" + AUTHOR_CHILD_NODE, author);
        }
    }

    private void updateMyNewsFeedAndWall(User author, Map<String, Object> childUpdates, String postId) {
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + author.getKey() + "/" + postId + "/" + AUTHOR_CHILD_NODE, author);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + author.getKey() + "/" + postId + "/" + AUTHOR_CHILD_NODE, author);
    }

    public interface OnGetFirebaseUserDataListener {
        void onSuccess(User user);

        void onFail(DatabaseError databaseError);
    }

}
