package com.example.translateconnector.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.darsh.multipleimageselect.models.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.asynctask.LoadMultiImagesTask;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.ResultImage;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FileUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FirebaseStorageUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imoktranslator.utils.FireBaseDataUtils.TIME_LINE;

public class PostStatusPresenter extends BaseImageUploadPresenter {
    private PostStatusView view;
    private DatabaseReference firebaseReference;
    private PostManager postManager;
    private List<FileModel> uploadedFiles = new ArrayList<>();
    private User currentUser;

    public PostStatusPresenter(Context context, PostStatusView view) {
        super(context, view);
        this.view = view;
        firebaseReference = FireBaseDataUtils.getInstance().getFirebaseReference();
        postManager = new PostManager();
        currentUser = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();
    }

    public void post(@Nullable Post post, String message, @Nullable List<FileModel> fileModels, String priorityMode) {
        if (currentUser != null) {
            boolean needPushNotify = false;
            if (post == null) {
                if (!priorityMode.equals(Constants.PRIVATE_MODE)) {
                    needPushNotify = true;
                }
                String postId = firebaseReference.child(TIME_LINE).child(FireBaseDataUtils.NEWS_FEED).push().getKey();
                post = new Post(postId, currentUser, message, System.currentTimeMillis(), priorityMode);
                post.setFileModels(fileModels);
            } else {
                post.setMessage(message);
                post.setMode(priorityMode);
                post.setTimestamp(System.currentTimeMillis());
            }

            boolean finalNeedPushNotify = needPushNotify;
            Post finalPost = post;
            postManager.updatePost(post, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    changePostPriority(priorityMode);
                    if (finalNeedPushNotify) {
                        getBestFriend(finalPost);
                    }
                } else {
                    //Remove uploaded image if comment fail
                    if (uploadedFiles != null) {
                        for (FileModel uploadedFile : uploadedFiles)
                        FirebaseStorageUtils.getInstance().deleteFile(uploadedFile);
                    }
                    view.firebaseError(databaseError);
                    view.hideProgress();
                }
                uploadedFiles.clear();
            });
        } else {
            view.hideProgress();
            throw new NullPointerException("Make a new post with null author");
        }
    }

    private void changePostPriority(String priorityMode) {
        if (priorityMode.equals(currentUser.getPrioritySetting())) {
            view.postStatusSuccess();
            view.hideProgress();
            return;
        }

        FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                .child(currentUser.getKey()).child("prioritySetting")
                .setValue(priorityMode, (databaseError, databaseReference) -> {
                    view.postStatusSuccess();
                    view.hideProgress();
                });
    }

    public void uploadMediaFile(Uri mediaUri, String type) {
        view.showProgress();
        FirebaseStorageUtils.getInstance().sendFileByUri(mediaUri,
                FileUtils.getFileName(mediaUri, getContext()), type,
                new FirebaseStorageUtils.OnUploadFileListener() {
                    @Override
                    public void onSuccess(FileModel fileModel) {
                        uploadedFiles.add(fileModel);
                        view.uploadMediaSuccess(fileModel);
                    }

                    @Override
                    public void onFail(Exception e) {
                        view.hideProgress();
                        view.notify(getContext().getString(R.string.TB_1053));
                    }
                });
    }

    public void uploadImage(List<Image> images) {
        LoadMultiImagesTask loadMultiImagesTask = new LoadMultiImagesTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                view.showProgress();
            }

            @Override
            protected void onPostExecute(List<ResultImage> resultImages) {
                super.onPostExecute(resultImages);
                if (!resultImages.contains(null)) {
                    uploadImagesToFirebase(resultImages);
                } else {
                    view.hideProgress();
                    view.notify(getContext().getString(R.string.TB_1067));
                }

            }
        };
        loadMultiImagesTask.execute(images);

    }

    private void uploadImagesToFirebase(List<ResultImage> resultImages) {

        for (ResultImage resultImage : resultImages) {
            FirebaseStorageUtils.getInstance().sendImageByBytes(resultImage.getBytes(), resultImage.getFile(),
                    new FirebaseStorageUtils.OnUploadFileListener() {
                        @Override
                        public void onSuccess(FileModel fileModel) {
                            uploadedFiles.add(fileModel);
                            if (uploadedFiles.size() == resultImages.size()) {
                                view.uploadImagesSuccess(uploadedFiles);
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            view.hideProgress();
                            view.notify(getContext().getString(R.string.TB_1053));
                        }
                    });
        }
    }

    public void requestRecordVideo(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            view.recordVideo();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void requestRecordAudioPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            view.startRecordAudio();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void getBestFriend(Post post) {
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.USER_FRIEND_COLLECTION)
                .child(currentUser.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> receiverKeys = new ArrayList<>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    try {
                        Friend friend = dsp.getValue(Friend.class);
                        if (friend.isBestFriend()) {
                            receiverKeys.add(friend.getUserKey());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getUserId(receiverKeys, post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserId(List<String> receiverKeys, Post post) {
        List<Integer> listReceiverId = new ArrayList<>();
        for (String userKey : receiverKeys) {
            FireBaseDataUtils.getInstance().getFirebaseReference().child(FireBaseDataUtils.USERS_COLLECTION)
                    .orderByChild("key").equalTo(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                        listReceiverId.add(user.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listReceiverId.size() == receiverKeys.size()) {
                        sendNotifyToBestFriend(post, listReceiverId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotifyToBestFriend(Post post, List<Integer> receiverIds) {
            Map<String, Object> params = new HashMap<>();
            params.put("receiver_ids", receiverIds);
            params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_BEST_FRIEND));
            params.put("model_id", post.getId());
            params.put("owner_id", post.getAuthor().getId());

            requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
                @Override
                public void onSuccess(Void response) {
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(int errCode, String errMessage) {
                    Log.e("Send Notification Post Status", "err: " + errMessage);
                }
            });
    }

    public interface PostStatusView extends BaseImageUploadView {
        void uploadImagesSuccess(List<FileModel> uploadedFile);

        void postStatusSuccess();

        void firebaseError(DatabaseError databaseError);

        void recordVideo();

        //upload Audio or video
        void uploadMediaSuccess(FileModel fileModel);

        void startRecordAudio();
    }
}
