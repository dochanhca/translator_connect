package com.example.translateconnector.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.adapter.ChatMemberAdapter;
import com.imoktranslator.callback.ChatInfoDialogClickListener;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.ChatGroupInfoPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.ImagesManager;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ChatGroupInfoDialog extends BaseDialog implements ChatMemberAdapter.ClickListener,
        ChatGroupInfoPresenter.ChatGroupInfoView {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    @BindView(R.id.img_group_avatar)
    CircleImageView imgGroupAvatar;
    @BindView(R.id.edt_group_name)
    OpenSansEditText edtGroupName;
    @BindView(R.id.txt_add_friend)
    OpenSansBoldTextView txtAddFriend;
    @BindView(R.id.rcv_member)
    RecyclerView rcvMember;
    @BindView(R.id.txt_view_more)
    OpenSansTextView txtViewMore;
    @BindView(R.id.img_mute_notification)
    AppCompatImageView imgMuteNotification;
    @BindView(R.id.txt_mute_notification)
    OpenSansSemiBoldTextView txtMuteNotification;

    private Room room;
    private UserRoom userRoom;
    private ChatInfoDialogClickListener listener;
    private String currentUserKey;

    private ChatMemberAdapter adapter;

    private ChatGroupInfoPresenter presenter;
    private File selectedFile;

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_chat_group;
    }

    @Override
    public void onStart() {
        super.onStart();

        int width = (int) (Utils.getScreenWidth(getContext()) * 0.9);
        setupDialog(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        presenter = new ChatGroupInfoPresenter(getActivity(), this);

        currentUserKey = LocalSharedPreferences.getInstance(getActivity()).getKeyUser();

        initViews();
    }

    private void initViews() {
        edtGroupName.setText(room.getChatRoomName());
        loadGroupAvatar(room);
        imgMuteNotification.setImageResource(userRoom.isMuteNotification()
                ? R.drawable.ic_enable_notification_blue : R.drawable.ic_disable_notification_blue);
        txtMuteNotification.setText(getString(userRoom.isMuteNotification()
                ? R.string.MH47_005 : R.string.MH47_006));

        adapter = new ChatMemberAdapter(getActivity().getApplicationContext(), getUserKeys(),
                currentUserKey.equals(room.getOwner()));
        adapter.setListener(this);
        rcvMember.setAdapter(adapter);

    }

    private List<String> getUserKeys() {
        List<String> keys = new ArrayList<>();
        keys.addAll(Arrays.asList(room.getVisitor().split(",")));
        keys.add(room.getOwner());
        keys.remove(currentUserKey);
        if (keys.size() > 4) {
            return keys.subList(0, 4);
        }

        return keys;
    }

    @OnClick({R.id.txt_add_friend, R.id.txt_view_more, R.id.layout_mute_notification,
            R.id.txt_leave_group, R.id.txt_cancel, R.id.img_edit, R.id.img_group_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_add_friend:
                listener.onAddMember(room);
                dismiss();
                break;
            case R.id.txt_view_more:
                listener.onViewAllMember(room);
                dismiss();
                break;
            case R.id.layout_mute_notification:
                listener.onMuteNotificationClick(userRoom);
                dismiss();
                break;
            case R.id.txt_leave_group:
                listener.onLeaveGroup(room, userRoom);
                dismiss();
                break;
            case R.id.txt_cancel:
                listener.onCancel(false);
                dismiss();
                break;
            case R.id.img_edit:
                editGroupName();
                break;
            case R.id.img_group_avatar:
                showPopup(imgGroupAvatar);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (selectedFile != null) {
                presenter.loadBitmapFormFile(selectedFile);

            }
        }
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            if (imageURI != null) {
                selectedFile = ImagesManager.getFileFromUri(getActivity(), imageURI);
                presenter.loadBitmapFormFile(selectedFile);
            }
        }
    }

    private void editGroupName() {
        if (edtGroupName.isEnabled()) {
            changeGroupName();
        } else {
            showKeyBoard();
            edtGroupName.setEnabled(true);
            edtGroupName.requestFocus();
            edtGroupName.setSelection(edtGroupName.getText().toString().length());
        }
    }

    private void changeGroupName() {
        DialogUtils.showProgress(getActivity());
        room.setChatRoomName(edtGroupName.getText().toString());
        edtGroupName.setEnabled(false);
        FireBaseDataUtils.getInstance().renameGroupChat(room, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                FireBaseDataUtils.getInstance().addMessageChangeGroupName(getActivity(), room);
                dismiss();
                listener.onCancel(true);
            } else {
                Toast.makeText(getActivity(), getString(R.string.TB_1053), Toast.LENGTH_SHORT).show();
            }
            DialogUtils.hideProgress();
        });
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtGroupName, InputMethodManager.SHOW_IMPLICIT);
    }

    private void loadGroupAvatar(Room room) {
        if (!TextUtils.isEmpty(room.getAvatar())) {
            Glide.with(getContext())
                    .load(room.getAvatar())
                    .error(R.drawable.img_avatar_default)
                    .into(imgGroupAvatar);
        } else {
            presenter.loadGroupAvatar(room);
        }
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_avatar_options);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.capture_image:
                    presenter.requestCameraPermission(getBaseActivity());
                    break;
                case R.id.pick_image:
                    presenter.requestReadSDCardPermission(getBaseActivity());
                    break;
            }
            return false;
        });
    }


    @Override
    public void onRemoveMember(User user) {
        dismiss();
        listener.onDeleteUser(room, user);
    }

    @Override
    public void onLoadGroupAvatar(TextDrawable textDrawable) {
        imgGroupAvatar.setImageDrawable(textDrawable);
    }

    @Override
    public void onChangeGroupAvatar(String urlAvatar) {
        room.setAvatar(urlAvatar);
        Glide.with(getContext())
                .load(room.getAvatar())
                .error(R.drawable.img_avatar_default)
                .into(imgGroupAvatar);
    }

    @Override
    public void openCamera() {
        String photoName = String.valueOf(System.currentTimeMillis()) + "_" + currentUserKey;
        selectedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), photoName + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                Constants.IMAGE_CAMERA_AUTHORITY,
                selectedFile);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Get Photo"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onLoadBitmap(byte[] bytes) {
        presenter.uploadImageToFirebase(room, bytes, selectedFile);
    }

    public static void showDialog(FragmentManager fragmentManager, Room room, UserRoom userRoom,
                                  ChatInfoDialogClickListener listener) {
        ChatGroupInfoDialog dialog = new ChatGroupInfoDialog();
        dialog.room = room;
        dialog.userRoom = userRoom;
        dialog.listener = listener;
        dialog.show(fragmentManager, dialog.getClass().getSimpleName());
    }

}
