package com.example.translateconnector.presenter;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonalMessagePresenter extends BasePresenter {

    private PersonalMessageView view;

    public PersonalMessagePresenter(Context context, PersonalMessageView view) {
        super(context);
        this.view = view;
    }

    public void getUserRoomsFromFireBase() {
        DialogUtils.showProgress(getContext());
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                .child(LocalSharedPreferences.getInstance(getContext()).getKeyUser()).orderByChild("lastTimeActive");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserRoom> listRoom = new ArrayList<>();
                List<UserRoom> personalRooms = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    UserRoom userRoom = dsp.getValue(UserRoom.class);
                    userRoom.setUserName("");
                    if (!userRoom.isDeleted() && userRoom.getLastTimeActive() > 0) {
                        listRoom.add(userRoom);
                        if (userRoom.getType() == FireBaseDataUtils.getInstance().ROOM_TYPE_PERSONAL) {
                            personalRooms.add(userRoom);
                        }
                    }
                }
                Collections.reverse(listRoom);
                Collections.reverse(personalRooms);
                view.onGetListRoom(listRoom, personalRooms);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onGetLisRoomError();
            }
        });
    }

    public void deleteUserRoom(UserRoom userRoom) {
        DialogUtils.showProgress(getContext());
        FireBaseDataUtils.getInstance().deleteUserRoom(getContext(), userRoom, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                view.onDeleteChatSuccess(userRoom);
            } else {
                view.notify(getContext().getString(R.string.TB_1053));
            }
            DialogUtils.hideProgress();
        });
    }

    public void muteNotification(UserRoom userRoom) {
        userRoom.setMuteNotification(!userRoom.isMuteNotification());
        DialogUtils.showProgress(getContext());
        FireBaseDataUtils.getInstance().muteNotification(userRoom, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                view.onMuteNotificationSuccess();
            } else {
                userRoom.setMuteNotification(!userRoom.isMuteNotification());
                view.notify(getContext().getString(R.string.TB_1053));
            }
            DialogUtils.hideProgress();
        });
    }


    public interface PersonalMessageView extends BaseView {
        void onGetListRoom(List<UserRoom> userRooms, List<UserRoom> personalRooms);

        void onGetLisRoomError();

        void onDeleteChatSuccess(UserRoom userRoom);

        void onMuteNotificationSuccess();
    }
}
