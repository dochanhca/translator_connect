package com.example.translateconnector.fragment;

import static android.app.Activity.RESULT_OK;
import static com.imoktranslator.utils.Constants.MAX_AUDIO_DURATION;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.imoktranslator.R;
import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.activity.ChatMembersActivity;
import com.imoktranslator.activity.ChatRoomActivity;
import com.imoktranslator.activity.DisplayImageProfileActivity;
import com.imoktranslator.activity.NewGroupChatActivity;
import com.imoktranslator.activity.OrderDetailActivity;
import com.imoktranslator.activity.UserDetailActivity;
import com.imoktranslator.activity.UserInfoActivity;
import com.imoktranslator.adapter.ChattingAdapter;
import com.imoktranslator.callback.ChatInfoDialogClickListener;
import com.imoktranslator.dialog.ChatGroupInfoDialog;
import com.imoktranslator.dialog.ChatPersonalInfoDialog;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.Message;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.presenter.ChatPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.ImagesManager;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatFragment extends BaseFragment implements ChattingAdapter.ChatAdapterClickListener,
        ChatPresenter.ChatView, ChatInfoDialogClickListener {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int CHANGE_MEMBER_REQUEST = 3;
    private static final int UPDATE_TO_GROUP_REQUEST = 4;

    //Views UI
    @BindView(R.id.rcv_message)
    RecyclerView rvListMessage;
    @BindView(R.id.img_send_message)
    ImageView btSendMessage;
    @BindView(R.id.img_emoji)
    ImageView btEmoji;
    @BindView(R.id.editTextMessage)
    EmojiconEditText edMessage;
    @BindView(R.id.contentRoot)
    View contentRoot;
    @BindView(R.id.layout_action)
    ViewGroup layoutAction;
    @BindView(R.id.img_record)
    ImageView btRecord;

    private EmojIconActions emojIcon;
    private LinearLayoutManager mLinearLayoutManager;

    //Firebase and GoogleApiClient
    private DatabaseReference mFirebaseDatabaseReference;
    private Query mQuery;
    //File
    private File filePathImageCamera;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private String roomKey;
    private boolean isOrderChat = false;
    private boolean isViewOnly;
    private int receiverId;
    private int orderId;
    private boolean needChangeHeaderTitle;
    private int chatType;

    private ChattingAdapter firebaseAdapter;
    private ChatPresenter chatPresenter;
    private boolean isRecording;
    private MediaRecorder recorder;

    private int playingPosition = -1;
    private MediaPlayer mp;

    public static ChatFragment newInstance(String roomKey, boolean isOrderChat, boolean isViewOnly,
                                           int receiverId, int orderId) {
        return newInstance(roomKey, isOrderChat, isViewOnly, receiverId, orderId, false,
                FireBaseDataUtils.ROOM_TYPE_PERSONAL);
    }

    public static ChatFragment newInstance(String roomKey, boolean isOrderChat, boolean isViewOnly,
                                           int receiverId, int orderId, boolean needChangeHeaderTitle) {
        return newInstance(roomKey, isOrderChat, isViewOnly, receiverId, orderId,
                needChangeHeaderTitle, FireBaseDataUtils.ROOM_TYPE_PERSONAL);
    }

    public static ChatFragment newInstance(String roomKey, boolean isOrderChat, boolean isViewOnly,
                                           int receiverId, int orderId, boolean needChangeHeaderTitle, int chatType) {
        ChatFragment fragment = new ChatFragment();
        fragment.roomKey = roomKey;
        fragment.isOrderChat = isOrderChat;
        fragment.isViewOnly = isViewOnly;
        fragment.receiverId = receiverId;
        fragment.orderId = orderId;
        fragment.needChangeHeaderTitle = needChangeHeaderTitle;
        fragment.chatType = chatType;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initViews() {
        chatPresenter = new ChatPresenter(getActivity(), this, roomKey,
                isOrderChat, receiverId, orderId, chatType);

        emojIcon = new EmojIconActions(getActivity(), contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        getMessageFromFirebase();
        layoutAction.setVisibility(isViewOnly ? View.GONE : View.VISIBLE);

        if (!isOrderChat && getChatRoomActivity() != null) {
            getChatRoomActivity().setRightButtonRes(R.drawable.ic_chat_info);
            getChatRoomActivity().setRightButtonClick(v -> {
                if (chatType == FireBaseDataUtils.ROOM_TYPE_PERSONAL) {
                    ChatPersonalInfoDialog.showDialog(getFragmentManager(), getChatRoomActivity().getPartnerUser(),
                            getChatRoomActivity().getUserRoom(), getChatRoomActivity().getRoom(), this);
                } else if (chatType == FireBaseDataUtils.ROOM_TYPE_GROUP) {
                    ChatGroupInfoDialog.showDialog(getFragmentManager(), getChatRoomActivity().getRoom(),
                            getChatRoomActivity().getUserRoom(), this);
                }
            });

            if (getChatRoomActivity().getRoom().isReadOnly()) {
                layoutAction.setVisibility(View.GONE);
                getChatRoomActivity().setRightBtnVisible(View.GONE);
            }

            chatPresenter.markReadMessage(getChatRoomActivity().getUserRoom());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        StorageReference storageRef;
        switch (requestCode) {
            case IMAGE_GALLERY_REQUEST:
                storageRef = storage.getReferenceFromUrl(FireBaseDataUtils.URL_STORAGE_REFERENCE).
                        child(FireBaseDataUtils.CHAT_STORAGE);
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        filePathImageCamera = ImagesManager.getFileFromUri(getActivity(),
                                selectedImageUri);
                        sendFileFirebase(storageRef, filePathImageCamera);
                    }
                }
                break;
            case IMAGE_CAMERA_REQUEST:
                storageRef = storage.getReferenceFromUrl(FireBaseDataUtils.URL_STORAGE_REFERENCE).
                        child(FireBaseDataUtils.CHAT_STORAGE);
                if (resultCode == RESULT_OK) {
                    if (filePathImageCamera != null && filePathImageCamera.exists()) {
                        sendFileFirebase(storageRef, filePathImageCamera);
                    }
                }
                break;
            case CHANGE_MEMBER_REQUEST:
                if (resultCode == RESULT_OK) {
                   changeMemberRequestResult(data);
                }
                break;
            case UPDATE_TO_GROUP_REQUEST:
                if (resultCode == RESULT_OK) {
                    updateToGroupResult(data);
                }
                break;
            default:
                break;


        }
    }

    private void changeMemberRequestResult(Intent data) {
        Room room = data.getParcelableExtra(Constants.ROOM_KEY);
        boolean isRoomRemoved = data.getBooleanExtra(Constants.IS_ROOM_REMOVED, false);
        getChatRoomActivity().setRoom(room);
        getChatRoomActivity().refreshHeaderTitle();
        if (isRoomRemoved) {
            getActivity().finish();
        }
    }

    private void updateToGroupResult(Intent data) {
        Room room = data.getParcelableExtra(Constants.ROOM_KEY);
        getChatRoomActivity().setRoom(room);
        getChatRoomActivity().refreshHeaderTitle();
        chatType = FireBaseDataUtils.ROOM_TYPE_GROUP;
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        BaseActivity activity = getBaseActivity();
        if (activity != null && activity instanceof OrderDetailActivity && needChangeHeaderTitle) {
            ((OrderDetailActivity) activity).setLeftFragmentTitle(getString(R.string.MH12_001));
        }
        return false;
    }

    @OnClick({R.id.img_send_message, R.id.img_capture_image, R.id.img_select_image, R.id.img_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_send_message:
                sendTextMessage();
                break;
            case R.id.img_capture_image:
                chatPresenter.requestCameraPermission(getActivity());
                break;
            case R.id.img_select_image:
                chatPresenter.requestReadSDCardPermission(getActivity());
                break;
            case R.id.img_record:
                if (isRecording) {
                    chatPresenter.stopRecord();
                } else {
                    chatPresenter.startRecord();
                }
                break;
            default:
                break;
        }
    }

    private void sendTextMessage() {
        if (edMessage.getText().toString().isEmpty()) {
            return;
        }

        chatPresenter.sendTextMessage(edMessage.getText().toString());
        edMessage.setText("");
    }

    private void getMessageFromFirebase() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mQuery = mFirebaseDatabaseReference.child(FireBaseDataUtils.ROOM_MESSAGES_COLLECTION).
                child(roomKey).child("messages");

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(mQuery, Message.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseAdapter = new ChattingAdapter
                (LocalSharedPreferences.getInstance(getActivity()).getKeyUser(),
                        options, this, getActivity(),
                        isOrderChat, roomKey);

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (rvListMessage != null && firebaseAdapter != null) {
                    rvListMessage.smoothScrollToPosition(firebaseAdapter.getItemCount() - 1);
                }
            }
        });

        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
        rvListMessage.setHasFixedSize(true);
    }

    /**
     * send file to firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file) {
        if (storageReference != null) {
            chatPresenter.sendImageToFirebase(storageReference, file);
        }
    }

    private ChatRoomActivity getChatRoomActivity() {
        return (ChatRoomActivity) getBaseActivity();
    }

    @Override
    public void onImageClick(String photoUrl) {
        DisplayImageProfileActivity.startActivity(this, photoUrl);
    }

    @Override
    public void onUserClick(int userId) {
        if (!isViewOnly) {
            UserDetailActivity.startActivity((BaseActivity) getActivity(), userId);
        }
    }

    @Override
    public void onAudioClick(FileModel file, int position) {
        //set up MediaPlayer

        if (playingPosition == position) {
            mp.stop();
            playingPosition = -1;
        } else {
            mp = new MediaPlayer();
            try {
                mp.setDataSource(file.getUrlFile());
                mp.prepare();
                mp.start();
                playingPosition = position;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAdapter.startListening();
        LocalSharedPreferences.getInstance(getContext()).saveBooleanData(Constants.KEY_CHATTING, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mp != null) {
            mp.release();
        }
        firebaseAdapter.stopListening();
        LocalSharedPreferences.getInstance(getContext()).saveBooleanData(Constants.KEY_CHATTING, false);

        if (recorder != null) {
            recordingStopped();
        }
    }

    @Override
    public void openCamera() {
        String photoName = String.valueOf(System.currentTimeMillis()) + "_" + roomKey;
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), photoName + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                Constants.IMAGE_CAMERA_AUTHORITY,
                filePathImageCamera);
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
        //not implement
    }

    @Override
    public void recordingStarted(String audioFile) {
        isRecording = true;
        btRecord.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.flickering_animation));

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(audioFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setMaxDuration(MAX_AUDIO_DURATION);
        recorder.setOnInfoListener((mr, what, extra) -> {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                chatPresenter.stopRecord();
            }
        });

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Chat Fragment", "prepare() failed");
        }
        recorder.start();
    }

    @Override
    public void recordingStopped() {
        isRecording = false;
        btRecord.clearAnimation();

        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    public void onMuteNotificationSuccess(boolean muteNotification) {
        Toast.makeText(getActivity(), getString(muteNotification
                ? R.string.MH47_010 : R.string.MH47_009), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void firebaseError() {
        Toast.makeText(getActivity(), getString(R.string.TB_1053), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLeaveRoomSuccess() {
        getActivity().finish();
    }

    @Override
    public void onRemoveRoom() {
        getActivity().finish();
    }

    @Override
    public Room getRoomMessage() {
        return getChatRoomActivity().getRoom();
    }

    @Override
    public void onMuteNotificationClick(UserRoom userRoom) {
        chatPresenter.muteNotification(userRoom);
    }

    /**
     * Use for chat personal only
     *
     * @param user
     */
    @Override
    public void onViewProfile(User user) {
        UserInfoActivity.startActivity(getBaseActivity(), user.getKey(), user.getId(), true);
    }

    /**
     * Use for chat personal only
     *
     */
    @Override
    public void onCreateGroup(Room room) {
        NewGroupChatActivity.startActivityForResult(this, room, getChatRoomActivity().getPartnerUser(),
                UPDATE_TO_GROUP_REQUEST);
    }

    /**
     * Use for chat group Only
     *
     * @param room
     * @param userRoom
     */
    @Override
    public void onLeaveGroup(Room room, UserRoom userRoom) {
        chatPresenter.onLeaveRoom(room, userRoom);
    }

    /**
     * Use for chat group Only
     *
     * @param room
     * @param user
     */
    @Override
    public void onDeleteUser(Room room, User user) {
        chatPresenter.removeUser(room, user);
    }

    @Override
    public void onAddMember(Room room) {
        NewGroupChatActivity.startActivityForResult(this, room, null,
                CHANGE_MEMBER_REQUEST);
    }

    @Override
    public void onCancel(boolean isNameChanged) {
        if (isNameChanged) {
            getChatRoomActivity().refreshHeaderTitle();
        }
    }

    @Override
    public void onViewAllMember(Room room) {
        ChatMembersActivity.startActivityForResult(this, room, CHANGE_MEMBER_REQUEST);
    }
}
