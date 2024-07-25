package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.ChatFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class ChatRoomActivity extends BaseActivity implements FragmentBox,
        HeaderView.BackButtonClickListener {

    @BindView(R.id.header_chat)
    HeaderView header;

    public static final String INTENT_ROOM = "INTENT_ROOM";
    public static final String TITLE_ROOM = "intent_room_title";

    private FragmentController fm;
    private String currentUserKey;
    private User partnerUser;
    private Room room;
    private UserRoom userRoom;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void initViews() {
        currentUserKey = LocalSharedPreferences.getInstance(this).getKeyUser();

        header.setCallback(this);
        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());
        userRoom = getIntent().getParcelableExtra(INTENT_ROOM);
        String title = getIntent().getStringExtra(TITLE_ROOM);

        if (title != null) {
            header.setTittle(title);
        }
        DialogUtils.showProgress(this);
        getTitleByRoom(userRoom.getRoomKey());
    }

    private void getTitleByRoom(String roomKey) {
        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION)
                .orderByChild("key").equalTo(roomKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            room = dsp.getValue(Room.class);
                            startChatFragment();
                            List<String> listVisitor = Arrays.asList(room.getVisitor().split("\\s*,\\s*"));
                            if (room.getType() == FireBaseDataUtils.ROOM_TYPE_GROUP) {
                                header.setTittle(room.getChatRoomName());
                                DialogUtils.hideProgress();
                            } else {
                                String keyUser = !room.getOwner().equals(currentUserKey)
                                        ? room.getOwner() : listVisitor.get(0);
                                Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                                        .child(FireBaseDataUtils.USERS_COLLECTION)
                                        .orderByChild("key").equalTo(keyUser);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                            partnerUser = dsp.getValue(User.class);
                                            header.setTittle(partnerUser.getName());
                                        }
                                        DialogUtils.hideProgress();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(ChatRoomActivity.this,
                                                getString(R.string.TB_1053), Toast.LENGTH_SHORT).show();
                                        DialogUtils.hideProgress();
                                        finish();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        DialogUtils.hideProgress();
                    }
                });
    }

    private void startChatFragment() {
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.REPLACE)
                .build();
        switchFragment(ChatFragment.newInstance(userRoom.getRoomKey(), false, false,
                -1, -1, false, userRoom.getType()), option);
    }

    public void setRightBtnVisible(int visible) {
        header.setBtnRightVisible(visible);
    }

    public void setRightButtonRes(int resId) {
        header.setImgRightRes(resId);
    }

    public void setRightButtonClick(View.OnClickListener onClickListener) {
        header.setBtnRightClick(onClickListener);
    }

    public User getPartnerUser() {
        return partnerUser;
    }

    public Room getRoom() {
        return room;
    }

    public UserRoom getUserRoom() {
        return userRoom;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void refreshHeaderTitle() {
        header.setTittle(room.getChatRoomName());
    }

    @Override
    public int getContainerViewId() {
        return R.id.container_chat;
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    public static void startActivity(BaseActivity activity, UserRoom userRoom) {
        startActivity(activity, userRoom, null);
    }

    public static void startActivity(BaseActivity activity, UserRoom userRoom, String title) {
        if (userRoom == null && TextUtils.isEmpty(userRoom.getRoomKey())) {
            return;
        }
        Intent intent = new Intent(activity, ChatRoomActivity.class);
        intent.putExtra(INTENT_ROOM, userRoom);
        intent.putExtra(TITLE_ROOM, title);
        activity.startActivity(intent);
    }

}
