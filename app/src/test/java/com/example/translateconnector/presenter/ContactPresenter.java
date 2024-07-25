package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.firebase.ContactManager;
import com.imoktranslator.model.UserFriend;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.imoktranslator.utils.FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION;

public class ContactPresenter extends BasePresenter {
    private ContactView view;
    private List<Friend> friendList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<UserFriend> userFriends = new ArrayList<>();
    private String currentUserKey;
    private ContactManager contactManager;

    public ContactPresenter(Context context, ContactView view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        contactManager = new ContactManager(currentUserKey, friendList, userList, userFriends);
    }

    public void deleteFriend(UserFriend userFriend) {
        view.showProgress();
        String userKey = currentUserKey;
        String friendKey = userFriend.getUser().getKey();
        FireBaseDataUtils.getInstance().removeFriend(userKey, friendKey, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                disableRoomChat(userFriend);
            } else {
                view.fireBaseError();
                view.hideProgress();
            }
        });
    }

    public void getFriends() {
        contactManager.getFriends(new ContactManager.OnGetDataListener() {
            @Override
            public void onSuccess(List<UserFriend> userFriends) {
                view.getDataSuccessful(userFriends);
            }

            @Override
            public void onFirebaseError() {
                view.fireBaseError();
            }
        });
    }


    public void filterFriend(String query) {
        List<UserFriend> result = new ArrayList<>();
        for (UserFriend userFriend : userFriends) {
            if (userFriend.getUser().getName().toLowerCase().contains(query.toLowerCase())) {
                result.add(userFriend);
            }
        }
        view.filterResult(result, query);
    }

    public void findRoomKey(String fireBaseFriendID, String friendName) {
        DialogUtils.showProgress(getContext());
        for (Friend friend : friendList) {
            if (friend.getUserKey().equals(fireBaseFriendID)) {
                final boolean[] isUserRoomExist = new boolean[1];

                FireBaseDataUtils.getInstance().getFirebaseReference()
                        .child(USER_CHAT_ROOM_COLLECTION)
                        .child(currentUserKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                    UserRoom userRoom = dsp.getValue(UserRoom.class);
                                    if (userRoom.getRoomKey().equals(friend.getRoomKey())) {
                                        view.getUserRoomSuccess(userRoom, friendName);
                                        DialogUtils.hideProgress();
                                        isUserRoomExist[0] = true;
                                        break;
                                    }
                                }
                                //Create new User Rooms to show Chat if not exist room
                                if (!isUserRoomExist[0]) {
                                    createNewUserRoom(friend.getRoomKey());
                                }
                            }

                            private void createNewUserRoom(String roomkey) {
                                UserRoom userRoom = new UserRoom(friend.getRoomKey(), currentUserKey, friend.getRoomKey(),
                                        0, Calendar.getInstance().getTimeInMillis());
                                userRoom.setType(FireBaseDataUtils.ROOM_TYPE_PERSONAL);

                                FireBaseDataUtils.getInstance().getFirebaseReference().child(USER_CHAT_ROOM_COLLECTION)
                                        .child(currentUserKey).child(roomkey).setValue(userRoom)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                view.getUserRoomSuccess(userRoom, friendName);
                                            } else {
                                                view.fireBaseError();
                                            }
                                            DialogUtils.hideProgress();
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                view.fireBaseError();
                                DialogUtils.hideProgress();
                            }
                        });

                break;
            }
        }
    }

    private void disableRoomChat(UserFriend userFriend) {
        FireBaseDataUtils.getInstance().disableRoomChat(userFriend.getFriend().getRoomKey(), (databaseError, databaseReference) -> {
            if (databaseError == null) {
                view.deleteFriendSuccessful();
            } else {
                view.fireBaseError();
            }
            view.hideProgress();
        });
    }

    public interface ContactView extends BaseView {
        void deleteFriendSuccessful();

        void fireBaseError();

        void getUserRoomSuccess(UserRoom userRoom, String friendName);

        void getDataSuccessful(List<UserFriend> userFriends);

        void filterResult(List<UserFriend> result, String query);
    }
}
