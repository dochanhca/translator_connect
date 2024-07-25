package com.example.translateconnector.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.callback.ChatInfoDialogClickListener;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPersonalInfoDialog extends BaseDialog {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    OpenSansSemiBoldTextView txtUserName;
    @BindView(R.id.txt_mute_notification)
    OpenSansSemiBoldTextView txtMuteNotification;
    @BindView(R.id.txt_view_profile)
    OpenSansSemiBoldTextView txtViewProfile;
    @BindView(R.id.txt_create_group)
    OpenSansSemiBoldTextView txtCreateGroup;
    @BindView(R.id.txt_cancel)
    OpenSansSemiBoldTextView txtCancel;
    @BindView(R.id.img_mute_notification)
    ImageView imgMuteNotification;

    private ChatInfoDialogClickListener listener;

    private User user;
    private UserRoom userRoom;
    private Room room;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    private void initViews() {
        txtUserName.setText(user.getName());
        Glide.with(this)
                .load(user.getAvatar())
                .error(R.drawable.img_avatar_default)
                .placeholder(R.drawable.img_avatar_default)
                .dontAnimate()
                .transform(new CircleTransform(getActivity()))
                .into(profileImage);
        imgMuteNotification.setImageResource(userRoom.isMuteNotification()
                ? R.drawable.ic_enable_notification_blue: R.drawable.ic_disable_notification_blue);
        txtMuteNotification.setText(getString(userRoom.isMuteNotification()
        ? R.string.MH47_005 : R.string.MH47_006));

    }

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_chat_personal;
    }

    @Override
    public void onStart() {
        super.onStart();

        int width = (int) (Utils.getScreenWidth(getContext()) * 0.9);
        setupDialog(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick({R.id.layout_mute_notification, R.id.txt_view_profile, R.id.txt_create_group, R.id.txt_cancel})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.layout_mute_notification:
                listener.onMuteNotificationClick(userRoom);
                break;
            case R.id.txt_view_profile:
                listener.onViewProfile(user);
                break;
            case R.id.txt_create_group:
                listener.onCreateGroup(room);
                break;
            case R.id.txt_cancel:
                break;
        }
    }

    public static void showDialog(FragmentManager fragmentManager, User user, UserRoom userRoom, Room room,
                           ChatInfoDialogClickListener listener) {
        ChatPersonalInfoDialog chatPersonalInfoDialog = new ChatPersonalInfoDialog();
        chatPersonalInfoDialog.user = user;
        chatPersonalInfoDialog.userRoom = userRoom;
        chatPersonalInfoDialog.room = room;
        chatPersonalInfoDialog.listener = listener;

        chatPersonalInfoDialog.show(fragmentManager, ChatPersonalInfoDialog.class.getSimpleName());
    }
}
