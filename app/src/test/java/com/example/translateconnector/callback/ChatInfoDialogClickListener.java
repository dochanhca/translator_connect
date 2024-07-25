package com.example.translateconnector.callback;

import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;

public interface ChatInfoDialogClickListener {
    void onMuteNotificationClick(UserRoom userRoom);

    void onViewProfile(User user);

    void onCreateGroup(Room room);

    void onLeaveGroup(Room room, UserRoom userRoom);

    void onDeleteUser(Room room, User user);

    void onAddMember(Room room);

    void onCancel(boolean isNameChanged);

    void onViewAllMember(Room room);

}
