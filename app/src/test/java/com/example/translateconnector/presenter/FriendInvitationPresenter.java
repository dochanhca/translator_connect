package com.example.translateconnector.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.model.FriendInvitationModel;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.Invitation;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class FriendInvitationPresenter extends BasePresenter {

    private FriendInvitationView view;
    private List<Invitation> invitationList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<FriendInvitationModel> data = new ArrayList<>();
    private PersonalInfo personalInfo;

    public FriendInvitationPresenter(Context context, FriendInvitationView view) {
        super(context);
        this.view = view;
        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
    }

    public void getFriendInvitations() {
        DialogUtils.showProgress(getContext());
        invitationList.clear();
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.FRIEND_INVITATION_COLLECTION)
                .child(view.getFireBaseUserId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Invitation invitation;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    invitation = dsp.getValue(Invitation.class);
                    invitationList.add(invitation);
                }

                if (invitationList.isEmpty()) {
                    view.emptyData();
                    DialogUtils.hideProgress();
                    return;
                }

                getListUserInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.fireBaseError();
                DialogUtils.hideProgress();
            }
        });
    }

    public void acceptFriendInvitation(FriendInvitationModel invitationModel) {
        view.showProgress();
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .orderByChild("key").equalTo(invitationModel.getSenderKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                            if (canAddFriend(user)) {
                                FireBaseDataUtils.getInstance().addNewFriend(
                                        view.getFireBaseUserId(), invitationModel.getSenderKey());
                                view.onAddFriend(invitationModel);
                            } else {
                                view.notify(getContext().getString(R.string.TB_1082));
                                view.onRemoveFriendRequest(invitationModel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            view.notify(getContext().getString(R.string.TB_1053));
                        }
                        view.hideProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.notify(getContext().getString(R.string.TB_1053));
                    }
                });

    }

    private boolean canAddFriend(User user) {
        return (user.isTranslator() && personalInfo.isTranslator())
                || (!user.isTranslator() && !personalInfo.isTranslator());
    }

    private void getListUserInfo() {
        userList.clear();
        for (Invitation invitation : invitationList) {
            queryUserInfo(invitation.getSenderKey());
        }
    }

    private synchronized void queryUserInfo(String senderKey) {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USERS_COLLECTION)
                .child(senderKey);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.add(dataSnapshot.getValue(User.class));

                if (userList.size() == invitationList.size()) {
                    processInvitationData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.fireBaseError();
                DialogUtils.hideProgress();
            }
        });
    }

    private void processInvitationData() {
        data.clear();
        for (int i = 0; i < userList.size(); i++) {
            data.add(new FriendInvitationModel(
                    userList.get(i).getId(),
                    userList.get(i).getName(),
                    userList.get(i).getKey(),
                    userList.get(i).getAvatar(),
                    invitationList.get(i).getMutualFriends()));
        }

        DialogUtils.hideProgress();
        view.getDataSuccessful(data);
    }

    public interface FriendInvitationView extends BaseView {

        String getFireBaseUserId();

        void fireBaseError();

        void getDataSuccessful(List<FriendInvitationModel> data);

        void emptyData();

        void onAddFriend(FriendInvitationModel invitationModel);

        void onRemoveFriendRequest(FriendInvitationModel invitationModel);
    }
}
