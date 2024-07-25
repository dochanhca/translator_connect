package com.example.translateconnector.presenter;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class GeneralNotificationPresenter extends BasePresenter {

    private GeneralNotificationView view;
    private String currentUserKey;

    public GeneralNotificationPresenter(Context context, GeneralNotificationView view) {
        super(context);
        this.view = view;
        currentUserKey = LocalSharedPreferences.getInstance(context).getKeyUser();
        getFriendInvitations();
    }

    private void getFriendInvitations() {
        List<Invitation> invitations = new ArrayList<>();
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.FRIEND_INVITATION_COLLECTION)
                .child(currentUserKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Invitation invitation = dsp.getValue(Invitation.class);
                    invitations.add(invitation);
                }

                view.onCountNotification(invitations.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public interface GeneralNotificationView extends BaseView {
        void onCountNotification(int numFriendRequest);
    }

}
