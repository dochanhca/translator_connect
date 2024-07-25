package com.example.translateconnector.presenter;

import static com.imoktranslator.utils.FireBaseDataUtils.COMMENTS;
import static com.imoktranslator.utils.FireBaseDataUtils.TIME_LINE;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.imoktranslator.R;
import com.imoktranslator.asynctask.LoadBitmapTask;
import com.imoktranslator.firebase.PostManager;
import com.imoktranslator.firebase.model.Comment;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.FirebaseStorageUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.NotificationHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PostDetailPresenter extends BaseImageUploadPresenter {

    private PostDetailView view;
    private PostManager postManager;
    private String currentUserId;

    public PostDetailPresenter(Context context, PostDetailView view) {
        super(context, view);
        this.view = view;
        postManager = new PostManager();
        currentUserId = LocalSharedPreferences.getInstance(context).getKeyUser();
    }

    public void deletePost(Post post) {
        view.showProgress();
        new PostManager().deletePost(post, (databaseError, databaseReference) -> {
            if (databaseError != null) {
            }
        });
    }

    public void comment(Post post, @Nullable String message, @Nullable FileModel fileModel) {
        String commentId = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(TIME_LINE)
                .child(COMMENTS)
                .child(post.getId())
                .push().getKey();

        User commenter = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser();
        Comment comment = new Comment(commentId, commenter, message, System.currentTimeMillis());
        comment.setFile(fileModel);
        postManager.addComment(post, post.getId(), comment, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                view.postCommentSuccess();
                view.hideProgress();
                sendNotify(post);
            } else {
                view.hideProgress();
                view.notify(getContext().getString(R.string.TB_1053));
                //Remove uploaded image if comment fail
                if (fileModel != null) {
                    FirebaseStorageUtils.getInstance().deleteFile(fileModel);
                }
            }
        });
    }

    private void sendNotify(Post post) {
        int commenterId = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser().getId();
        if (commenterId == post.getAuthor().getId()) {
            sendNotifyAuthorComment(post);
        } else {
            sendNotifyOtherUserComment(post);
        }
    }

    private void sendNotifyOtherUserComment(Post post) {
        String commenterKey = LocalSharedPreferences.getInstance(getContext()).getCurrentFirebaseUser().getKey();
        Map<String, Integer> commentators = post.getCommentators();
        if (commentators.containsKey(commenterKey)) {
            commentators.remove(commenterKey);
        }

        if (!commentators.containsKey(post.getAuthor().getKey())) {
            commentators.put(post.getAuthor().getKey(), post.getAuthor().getId());
        }
        //because of above step, commentators can not be empty

        Integer[] receiverIds = new Integer[commentators.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : commentators.entrySet()) {
            receiverIds[i] = entry.getValue();
            i++;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("receiver_ids", receiverIds);
        params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_OTHER_USER_COMMENT));
        params.put("model_id", post.getId());
        params.put("owner_id", post.getAuthor().getId());

        requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                Log.e("TonTN", "err: " + errMessage);
            }
        });

    }

    private void sendNotifyAuthorComment(Post post) {
        Map<String, Integer> commentators = post.getCommentators();
        if (commentators.containsKey(post.getAuthor().getKey())) {
            commentators.remove(post.getAuthor().getKey());
        }
        if (!commentators.isEmpty()) {
            Integer[] receiverIds = new Integer[commentators.size()];
            int i = 0;
            for (Map.Entry<String, Integer> entry : commentators.entrySet()) {
                receiverIds[i] = entry.getValue();
                i++;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("receiver_ids", receiverIds);
            params.put("type", Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_AUTHOR_COMMENT));
            params.put("model_id", post.getId());

            requestAPI(getAPI().sendNotification(params), new BaseRequest<Void>() {
                @Override
                public void onSuccess(Void response) {
                }

                @Override
                public void onFailure(int errCode, String errMessage) {
                    Log.e("TonTN", "err: " + errMessage);
                }
            });
        }
    }

    public void uploadImageToFirebase(byte[] bytes, File file) {
        view.showProgress();
        FirebaseStorageUtils.getInstance().sendImageByBytes(bytes, file, new FirebaseStorageUtils.OnUploadFileListener() {
            @Override
            public void onSuccess(FileModel fileModel) {
                view.uploadFileSuccess(fileModel);
            }

            @Override
            public void onFail(Exception e) {
                view.hideProgress();
                view.notify(getContext().getString(R.string.TB_1053));
            }
        });
    }

    public void loadBitmapFormFile(File file) {
        LoadBitmapTask loadBitmapTask = new LoadBitmapTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                view.onLoadBitmap(bytes);
            }
        };
        loadBitmapTask.execute(file);
    }

    public void getPostBy(String postId) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.TIME_LINE)
                .child(FireBaseDataUtils.POSTS)
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post != null) {
                            if (!currentUserId.equals(post.getAuthor().getKey()) &&
                                    Constants.PRIVATE_MODE.equals(post.getMode())) {
                                view.notAuthorizedPost();
                            } else {
                                view.onGetPostByIdSuccessful(post);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public interface PostDetailView extends BaseImageUploadPresenter.BaseImageUploadView {
        void uploadFileSuccess(FileModel file);

        void onLoadBitmap(byte[] bytes);

        void postCommentSuccess();

        void onGetPostByIdSuccessful(Post post);

        void notAuthorizedPost();
    }
}
