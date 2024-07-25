package com.example.translateconnector.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imoktranslator.R;
import com.imoktranslator.asynctask.LoadBitmapTask;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.Message;
import com.imoktranslator.model.firebase.Room;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.model.firebase.UserRoom;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;
import com.imoktranslator.utils.SerialUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatPresenter extends BaseImageUploadPresenter {

    private ChatView chatView;
    private String roomKey;
    private PersonalInfo personalInfo;
    private boolean isOrderChat;
    private String currentUserKey;
    private int receiverId;
    private int orderId;
    private String audioFile;
    private int chatType;

    public ChatPresenter(Context context, ChatView chatView, String roomKey,
                         boolean isOrderChat, int receiverId, int orderId, int chatType) {
        super(context, chatView);
        this.chatView = chatView;
        this.roomKey = roomKey;
        this.isOrderChat = isOrderChat;
        this.receiverId = receiverId;
        this.orderId = orderId;
        this.chatType = chatType;

        personalInfo = LocalSharedPreferences.getInstance(context).getPersonalInfo();
        currentUserKey = LocalSharedPreferences.getInstance(getContext()).getKeyUser();
        audioFile = getContext().getExternalCacheDir().getAbsolutePath() + "/audio_record.mp3";
    }

    public void sendTextMessage(String text) {
        Message message = new Message(currentUserKey, text,
                Calendar.getInstance().getTime().getTime() + "", null, null,
                personalInfo.getId());
        addMessageToFireBase(message);
    }

    private void addMessageToFireBase(Message message) {
        if (isOrderChat) {
            FireBaseDataUtils.getInstance().addMessageInChatOrder(roomKey, message);
            sendOrderMessageNotify();
        } else {
            FireBaseDataUtils.getInstance().addMessage(getContext(), roomKey, message, chatType);
            sendChatMessageNotify();
        }
    }

    private void sendChatMessageNotify() {
        Room room = chatView.getRoomMessage();
        List<String> listVisitor = new ArrayList<>();
        listVisitor.addAll(Arrays.asList(room.getVisitor().split(",")));
        listVisitor.add(room.getOwner());
        if (listVisitor.contains(currentUserKey)) {
            listVisitor.remove(currentUserKey);
        }
        List<String> receiverKeys = new ArrayList<>();

        final int[] i = new int[1];
        for (String userKey : listVisitor) {
            FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION)
                    .child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    i[0]++;
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        UserRoom userRoom = dsp.getValue(UserRoom.class);
                        if (userRoom.getRoomKey().equals(room.getKey())
                                && !userRoom.isMuteNotification()) {
                            receiverKeys.add(userRoom.getUserKey());
                        }
                    }
                    if (i[0] == listVisitor.size()) {
                        getUserId(receiverKeys);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    i[0]++;
                }
            });
        }
    }

    private void getUserId(List<String> receiverKeys) {
        List<Integer> listReceiverId = new ArrayList<>();
        for (String userKey : receiverKeys) {
            FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                    .orderByChild("key").equalTo(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    listReceiverId.add(user.getId());
                    if (listReceiverId.size() == receiverKeys.size()) {
                        sendNotify(listReceiverId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotify(List<Integer> listReceiverId) {
        Integer[] receiverIds = new Integer[listReceiverId.size()];
        receiverIds = listReceiverId.toArray(receiverIds);

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_CHAT));
        params.put("room_key", roomKey);
        requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                //
            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    private void sendOrderMessageNotify() {
        Map<String, Object> params = new HashMap<>();
        params.put("receiver_id", receiverId);
        params.put("order_id", orderId);
        requestAPI(getAPI().sendChatNotification(params), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {

            }

            @Override
            public void onFailure(int errCode, String errMessage) {

            }
        });
    }

    public void sendFileByUri(StorageReference storageReference, Uri file, String fileType) {
        final String name = String.valueOf(System.currentTimeMillis()) + "_" + personalInfo.getId();
        StorageReference uploadFile = storageReference.child(name);

        UploadTask uploadTask = uploadFile.putFile(file);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            // Continue with the task to get the download URL
            return uploadFile.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            Uri downloadUrl = task.getResult();
            FileModel fileModel = new FileModel(fileType, downloadUrl.toString(), name, "");

            sentFileMessage(fileModel);
        });
    }

    public void sendImageToFirebase(StorageReference storageReference, File file) {
        LoadBitmapTask loadBitmapTask = new LoadBitmapTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                StorageReference imageCameraRef = storageReference.child(file.getName() + "_camera");

                imageCameraRef.putBytes(bytes).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageCameraRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        FileModel fileModel = new FileModel("img", downloadUri.toString(), file.getName(),
                                file.length() + "");
                        sentFileMessage(fileModel);
                    } else {
                        // Handle failures
                        // ...
                    }
                });
            }
        };
        loadBitmapTask.execute(file);
    }

    private void sentFileMessage(FileModel fileModel) {
        Message message = new Message(currentUserKey, "",
                Calendar.getInstance().getTime().getTime() + "", fileModel, null,
                personalInfo.getId());
        addMessageToFireBase(message);
    }

    public void requestRecordAudioPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        chatView.recordingStarted(audioFile);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void stopRecord() {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(FireBaseDataUtils.URL_STORAGE_REFERENCE)
                .child(FireBaseDataUtils.CHAT_STORAGE);

        sendFileByUri(storageRef, Uri.fromFile(new File(audioFile)), FireBaseDataUtils.TYPE_AUDIO);
        chatView.recordingStopped();
    }

    public void muteNotification(UserRoom userRoom) {
        userRoom.setMuteNotification(!userRoom.isMuteNotification());
        DialogUtils.showProgress(getContext());
        FireBaseDataUtils.getInstance().muteNotification(userRoom, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                chatView.onMuteNotificationSuccess(userRoom.isMuteNotification());
            } else {
                userRoom.setMuteNotification(!userRoom.isMuteNotification());
                chatView.notify(getContext().getString(R.string.TB_1053));
            }
            DialogUtils.hideProgress();
        });
    }

    public void startRecord() {
        requestRecordAudioPermission((Activity) getContext());
    }

    public void removeUser(Room room, User user) {
        DialogUtils.showProgress(getContext());
        //clone object to keep original data if update room failed
        Room clone = (Room) SerialUtils.cloneObject(room);
        FireBaseDataUtils.getInstance().removeUserInRoom(getContext(), user.getKey(), user.getId(),
                user.getName(), room, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        chatView.firebaseError();
                        room.setOwner(clone.getOwner());
                        room.setVisitor(clone.getVisitor());
                    } else if (room.getVisitor().isEmpty()) {
                        chatView.onRemoveRoom();
                    }
                    DialogUtils.hideProgress();
                });
    }

    public void onLeaveRoom(Room room, UserRoom userRoom) {
        DialogUtils.showProgress(getContext());
        //clone object to keep original data if update room failed
        Room clone = (Room) SerialUtils.cloneObject(room);
        FireBaseDataUtils.getInstance().removeUserInRoom(getContext(), userRoom.getUserKey(),
                personalInfo.getId(), personalInfo.getName(), room, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        chatView.onLeaveRoomSuccess();
                    } else {
                        room.setOwner(clone.getOwner());
                        room.setVisitor(clone.getVisitor());
                        chatView.firebaseError();
                    }
                    DialogUtils.hideProgress();
                });
    }

    public void markReadMessage(UserRoom userRoom) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_CHAT_ROOM_COLLECTION).child(userRoom.getUserKey())
                .child(userRoom.getRoomKey()).child("hasUnreadMessage")
                .setValue(false);
    }

    public interface ChatView extends BaseImageUploadView {
        void recordingStarted(String audioFile);

        void recordingStopped();

        void onMuteNotificationSuccess(boolean muteNotification);

        void firebaseError();

        void onLeaveRoomSuccess();

        void onRemoveRoom();

        Room getRoomMessage();
    }
}
