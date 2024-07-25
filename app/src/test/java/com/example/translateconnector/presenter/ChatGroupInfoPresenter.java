package com.example.translateconnector.presenter;

import android.content.Context;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FirebaseStorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatGroupInfoPresenter extends BaseImageUploadPresenter {

    private ChatGroupInfoView view;


    public ChatGroupInfoPresenter(Context context, ChatGroupInfoView chatGroupInfoView) {
        super(context, chatGroupInfoView);
        this.view = chatGroupInfoView;
    }

    public void loadGroupAvatar(Room room) {
        List<String> userKeys = new ArrayList<>();
        userKeys.add(room.getOwner());
        userKeys.addAll(Arrays.asList(room.getVisitor().split("\\s*,\\s*")));
        final String[] avatar = {""};
        for (int i = 0; i < 2; i++) {
            Query query = FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                    .orderByChild("key").equalTo(userKeys.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    avatar[0] += user.getName().substring(0, 1);
                    if (avatar[0].length() == 2) {
                        TextDrawable drawable = TextDrawable.builder()
                                .beginConfig()
                                .width(120).height(120)
                                .fontSize(getContext().getResources().getDimensionPixelSize(R.dimen.text_common))
                                .endConfig()
                                .buildRound(avatar[0], getContext().getResources().getColor(R.color.colorPrimary));
                        view.onLoadGroupAvatar(drawable);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FireBase", ">>> Error:" + "find onCancelled:" + databaseError);
                }
            });
        }
    }

    public void uploadImageToFirebase(Room room, byte[] bytes, File file) {
        view.showProgress();
        FirebaseStorageUtils.getInstance().sendImageByBytes(bytes, file, new FirebaseStorageUtils.OnUploadFileListener() {
            @Override
            public void onSuccess(FileModel fileModel) {
                FireBaseDataUtils.getInstance().setRoomChatAvatar(room.getKey(),
                        fileModel.getUrlFile(), (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                view.onChangeGroupAvatar(fileModel.getUrlFile());
                            } else {
                                view.notify(getContext().getString(R.string.TB_1053));
                            }
                            view.hideProgress();
                        });
            }

            @Override
            public void onFail(Exception e) {
                view.hideProgress();
                view.notify(getContext().getString(R.string.TB_1053));
            }
        });
    }

    public interface ChatGroupInfoView extends BaseImageUploadView {
        void onLoadGroupAvatar(TextDrawable textDrawable);

        void onChangeGroupAvatar(String urlAvatar);
    }
}
