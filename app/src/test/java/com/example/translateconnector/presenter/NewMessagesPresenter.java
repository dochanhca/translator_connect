package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.firebase.ContactManager;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.UserFriend;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NewMessagesPresenter extends BasePresenter {
    private NewMessagesView view;

    private List<Friend> friendList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<UserFriend> userFriends = new ArrayList<>();
    private String currentUserKey;
    private ContactManager contactManager;
    private PersonalInfo personalInfo;

    public NewMessagesPresenter(Context context, NewMessagesView view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        contactManager = new ContactManager(currentUserKey, friendList, userList, userFriends);
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void getFriends(Room room) {
        view.showProgress();
        contactManager.getFriends(new ContactManager.OnGetDataListener() {
            @Override
            public void onSuccess(List<UserFriend> userFriends) {
                if (room == null) {
                    view.getDataSuccessful(userList);
                } else {
                    filterFriends(userList, room);
                }
                view.hideProgress();
            }

            @Override
            public void onFirebaseError() {
                view.fireBaseError();
                view.hideProgress();
            }
        });
    }

    private void filterFriends(List<User> userList, Room room) {
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(room.getVisitor().split(",")));
        for (Iterator<User> iterator = userList.listIterator(); iterator.hasNext(); ) {
            User user = iterator.next();
            if (keys.contains(user.getKey()) || room.getOwner().equals(user.getKey())) {
                iterator.remove();
            }
        }
        view.getDataSuccessful(userList);
    }


    public void query(String text) {
        List<User> result = new ArrayList<>();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(text)) {
                result.add(user);
            }
        }

        view.filterResult(result, text);
    }

    public void addNewGroup(List<User> users, User partner) {
        DialogUtils.showProgress(getContext());

        String groupName = personalInfo.getName();
        List<String> keys = new ArrayList<>();


        if (partner != null) {
            groupName += ", " + partner.getName();
            keys.add(partner.getKey());
        }
        for (User user : users) {
            groupName += ", " + user.getName();
            keys.add(user.getKey());
        }

        final String name = groupName.substring(0, groupName.length() - 2);
        String roomKey = FireBaseDataUtils.getInstance().addNewGroupChat(getContext(), name,
                currentUserKey, keys, personalInfo.getId(), personalInfo.getName());
        final boolean[] isUserRoomExist = new boolean[1];

        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                .child(currentUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    UserRoom userRoom = dsp.getValue(UserRoom.class);
                    if (userRoom.getRoomKey().equals(roomKey)) {
                        view.onCreateRoomChat(userRoom, name);
                        isUserRoomExist[0] = true;
                        break;
                    }
                }
                if (!isUserRoomExist[0]) {
                    view.fireBaseError();
                }
                view.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                view.hideProgress();
                view.fireBaseError();
            }
        });
    }

    public void addUserToRoom(List<User> users, Room room) {
        view.showProgress();
        String visitors = room.getVisitor();
        for (User user : users) {
            visitors += "," + user.getKey();
        }

        String finalVisitors = visitors;
        FireBaseDataUtils.getInstance().addUserToGroup(getContext(), room, users, visitors, (task) -> {
            if (task.isSuccessful()) {
                room.setVisitor(finalVisitors);
                view.addUserSuccess();
                view.hideProgress();
            } else {
                view.fireBaseError();
                view.hideProgress();
            }
        });
    }

    public interface NewMessagesView extends BaseView {
        void filterResult(List<User> result, String query);

        void fireBaseError();

        void getDataSuccessful(List<User> userList);

        void onCreateRoomChat(UserRoom userRoom, String roomName);

        void addUserSuccess();
    }
}
