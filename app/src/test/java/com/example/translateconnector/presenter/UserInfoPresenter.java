package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.SearchFriend;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FriendManager;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoPresenter extends BasePresenter {

    private String currentUserKey;
    private PersonalInfo personalInfo;
    private View view;

    public UserInfoPresenter(Context context, View view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void getUserDetail(int id, String userKey, boolean isFriend) {
        view.showProgress();
        requestAPI(getAPI().fetchUserInfo(id), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                if (!isFriend) {
                    checkFriendRequestSent(response.getPersonalInfo(), userKey);
                } else {
                    getFriendInfo(response.getPersonalInfo(), userKey);
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void getFriendInfo(PersonalInfo personalInfo, String userKey) {
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(currentUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Friend friend = dsp.getValue(Friend.class);
                    if (friend.getUserKey().equals(userKey)) {
                        view.onGetUserDetail(personalInfo, true,
                                friend.isBestFriend(), null);
                        break;
                    }
                }
                view.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                view.onGetUserDetail(personalInfo, true,false, null);
                view.hideProgress();
            }
        });
    }

    public void setBestFriend(String userKey, boolean isBestFriend) {
        view.showProgress();
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(currentUserKey)
                .child(userKey)
                .child("bestFriend")
                .setValue(isBestFriend, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        FireBaseDataUtils.getInstance().getFirebaseReference()
                                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                                .child(userKey)
                                .child(currentUserKey)
                                .child("bestFriend")
                                .setValue(isBestFriend, (error, reference) -> {
                                    if (databaseError == null) {
                                        view.onSetBestFriend(isBestFriend);
                                    } else {
                                        view.notify(getContext().getString(R.string.TB_1053));
                                    }
                                });
                    } else {
                        view.notify(getContext().getString(R.string.TB_1053));
                    }
                    view.hideProgress();
                });
    }

    public void updateLike(Post post) {
        String userKeyLikePost = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser().getKey();
        boolean isLikePost = !post.getLikes().containsKey(userKeyLikePost);
        new PostManager().updateLike(post, userKeyLikePost, (databaseError, databaseReference) -> {
            if (!userKeyLikePost.equals(post.getAuthor().getKey())) {
                if (isLikePost) {
                    sendNotify(post.getAuthor().getId(), post.getId());
                }
            }
        });
    }

    private void sendNotify(int receiverId, String postId) {
        Integer[] receiverIds = new Integer[1];
        receiverIds[0] = receiverId;

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_LIKE));
        params.put("model_id", postId);

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

    private void checkFriendRequestSent(PersonalInfo personalInfo, String userKey) {
        boolean[] isRequestSent = new boolean[1];
        FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.FRIEND_INVITATION_COLLECTION)
                .child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Invitation invitation = dsp.getValue(Invitation.class);
                    if (invitation.getSenderKey().equals(currentUserKey)) {
                        isRequestSent[0] = true;
                        break;
                    }
                }
                if (isRequestSent[0]) {
                    view.onGetUserDetail(personalInfo, isRequestSent[0], false, null);
                    view.hideProgress();
                } else {
                    getFirebaseInfo(personalInfo, userKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getFirebaseInfo(personalInfo, userKey);
            }
        });
    }

    /**
     *
     * @param personalInfo
     * @param userKey
     * Count mulual friends to send friend request
     */
    private void getFirebaseInfo(PersonalInfo personalInfo, String userKey) {
        FireBaseDataUtils.getInstance().getFirebaseReference().
                child(FireBaseDataUtils.USERS_COLLECTION).orderByChild("key").equalTo(userKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                            List<User> users = new ArrayList<>();
                            users.add(user);
                            FriendManager.getInstance(getContext()).countMutualFriends(users,
                                    searchFriends -> view.onGetUserDetail(personalInfo, false,
                                            false, searchFriends.get(0)));
                        } catch (Exception e) {
                            view.notify(getContext().getString(R.string.TB_1053));
                        }
                        view.hideProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.notify(getContext().getString(R.string.TB_1053));
                        view.hideProgress();
                    }
                });
    }

    public void sendFriendInvitation(SearchFriend item) {
        Invitation invitation = new Invitation(currentUserKey
                , personalInfo.getId(), item.getMutualFriend());
        FireBaseDataUtils.getInstance().sendInvitation(invitation, item.getKey(), (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        sendNotify(item.getId());
                    }
                }
        );
    }

    public void deleteFriend(String fireBaseFriendID) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(currentUserKey)
                .child(fireBaseFriendID)
                .removeValue()
                .addOnSuccessListener(aVoid -> view.onDeleteFriend());

        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(fireBaseFriendID)
                .child(currentUserKey)
                .removeValue()
                .addOnSuccessListener(aVoid -> {

                });
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
        void onGetUserDetail(PersonalInfo personalInfo, boolean isFriendRequestSent,
                             boolean isBestFriend, @Nullable SearchFriend searchFriend);

        void onSetBestFriend(boolean isBestFriend);

        void onDeleteFriend();
    }

}
