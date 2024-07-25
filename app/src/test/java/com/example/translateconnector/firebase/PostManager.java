package com.example.translateconnector.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.imoktranslator.firebase.model.Comment;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.firebase.model.PostStats;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.FireBaseDataUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.imoktranslator.utils.FireBaseDataUtils.COMMENTATORS_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.COMMENTS;
import static com.imoktranslator.utils.FireBaseDataUtils.COMMENTS_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.COMMENT_IDS_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.COMMENT_STATS_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.NEWS_FEED;
import static com.imoktranslator.utils.FireBaseDataUtils.POSTS;
import static com.imoktranslator.utils.FireBaseDataUtils.POST_ID_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.POST_STATS_CHILD_NODE;
import static com.imoktranslator.utils.FireBaseDataUtils.TIME_LINE;
import static com.imoktranslator.utils.FireBaseDataUtils.USERS_COLLECTION;
import static com.imoktranslator.utils.FireBaseDataUtils.WALL;

public class PostManager {

    public void deletePost(Post post, DatabaseReference.CompletionListener completionListener) {
        User author = post.getAuthor();
        String postId = post.getId();
        String userKey = author.getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        //1. Xoa bai viet trong posts, news feed cua user, wall cua user
        childUpdates.put("/" + TIME_LINE + "/" + POSTS + "/" + postId, null);
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + userKey + "/" + postId, null);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + userKey + "/" + postId, null);

        //2. Xoa bai viet trong news feed cua nhung nguoi nhin thay bai viet nay
        Set<String> friendKeyList = author.getPostStats().get(postId).getViewer().keySet();
        for (String friendKey : friendKeyList) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId, null);
        }

        //3. Xoa bai viet trong Post_stats
        childUpdates.put("/" + USERS_COLLECTION + "/" + userKey + "/" + POST_STATS_CHILD_NODE + "/" + postId, null);

        //4. Xoa tat ca binh luan trong node Comments cua bai viet nay
        childUpdates.put("/" + TIME_LINE + "/" + COMMENTS + "/" + postId, null);

        //5. Xoa tat ca comment_id lien quan toi bai viet nay trong commentStats cua nhung User da tung binh luan bai viet nay
        Set<String> commenterKeySet = post.getCommentators().keySet();
        for (String commenterKey: commenterKeySet) {
            childUpdates.put("/" + USERS_COLLECTION + "/" + commenterKey + "/" + COMMENT_STATS_CHILD_NODE + "/" + postId, null);
        }

        FireBaseDataUtils.getInstance().getFirebaseReference().updateChildren(childUpdates, completionListener);
    }

    /**
     *
     * @param post
     * @param completionListener
     * Create new post or update an existing post
     */
    public void updatePost(Post post, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference firebaseReference = FireBaseDataUtils.getInstance().getFirebaseReference();
        User author = post.getAuthor();
        String userKey = author.getKey();
        String postId = post.getId();
        Map<String, PostStats> postStatsMap = author.getPostStats();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TIME_LINE + "/" + POSTS + "/" + postId, post);
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + userKey + "/" + postId, post);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + userKey + "/" + postId, post);
        for (String friendKey : author.getFriends().keySet()) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId, post);
        }

        postStatsMap.put(postId, createPostStats(post));
        childUpdates.put("/" + USERS_COLLECTION + "/" + userKey + "/" + POST_STATS_CHILD_NODE, postStatsMap);

        firebaseReference.updateChildren(childUpdates, completionListener);
    }

    private PostStats createPostStats(Post post) {
        Map<String, Boolean> viewer = new HashMap<>();
        for (String friendKey : post.getAuthor().getFriends().keySet()) {
            viewer.put(friendKey, true);
        }

        return new PostStats(post.getId(), viewer);
    }

    public void updateLike(Post post, String userKeyLikePost, DatabaseReference.CompletionListener listener) {
        FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(TIME_LINE)
                .child(POSTS)
                .child(post.getId())
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Post post1 = mutableData.getValue(Post.class);
                        if (post1 == null) {
                            return Transaction.success(mutableData);
                        }

                        if (post1.getLikes().containsKey(userKeyLikePost)) {
                            // Unlike the post and remove self from likes
                            post1.getLikes().remove(userKeyLikePost);
                        } else {
                            // Like the post and add self to likes
                            post1.getLikes().put(userKeyLikePost, true);
                        }

                        // Set value and report transaction success
                        mutableData.setValue(post1);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (databaseError == null) {
                            Post post1 = dataSnapshot.getValue(Post.class);
                            if (post1 != null) {
                                updateLikeAllPlace(post1, listener);
                            }
                        }
                    }
                });
    }

    private void updateLikeAllPlace(Post post, DatabaseReference.CompletionListener listener) {
        User author = post.getAuthor();
        String postId = post.getId();
        String authorKey = author.getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TIME_LINE + "/" + POSTS + "/" + postId, post);
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + authorKey + "/" + postId, post);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + authorKey + "/" + postId, post);
        Set<String> friendKeyList = post.getViewer().keySet();
        for (String friendKey : friendKeyList) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId, post);
        }

        FireBaseDataUtils.getInstance().getFirebaseReference().updateChildren(childUpdates, listener);
    }

    public void addComment(Post post, String postId, Comment comment,
                           DatabaseReference.CompletionListener completionListener) {
        String commenterKey = comment.getAuthor().getKey();
        int commenterId = comment.getAuthor().getId();
        String postAuthorKey = post.getAuthor().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        //1. add comment to Comments node
        childUpdates.put("/" + TIME_LINE + "/" + COMMENTS + "/" + postId + "/" + comment.getId(), comment);

        //2. add comment stats to commenter
        childUpdates.put("/" + USERS_COLLECTION + "/" + commenterKey + "/" + COMMENT_STATS_CHILD_NODE + "/" + postId + "/" + POST_ID_CHILD_NODE, postId);
        childUpdates.put("/" + USERS_COLLECTION + "/" + commenterKey + "/" + COMMENT_STATS_CHILD_NODE + "/" + postId + "/" + COMMENT_IDS_CHILD_NODE + "/" + comment.getId(), true);

        //3. add commenter key to all location of this post
        childUpdates.put("/" + TIME_LINE + "/" + POSTS + "/" + postId + "/" + COMMENTATORS_CHILD_NODE + "/" + commenterKey, commenterId);
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + postAuthorKey + "/" + postId + "/" + COMMENTATORS_CHILD_NODE + "/" + commenterKey, commenterId);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + postAuthorKey + "/" + postId + "/" + COMMENTATORS_CHILD_NODE + "/" + commenterKey, commenterId);
        for (String friendKey : post.getViewer().keySet()) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId + "/" + COMMENTATORS_CHILD_NODE + "/" + commenterKey, commenterId);
        }

        //4. add comment id to all location of this post
        childUpdates.put("/" + TIME_LINE + "/" + POSTS + "/" + postId + "/" + COMMENTS_CHILD_NODE + "/" + comment.getId(), true);
        childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + postAuthorKey + "/" + postId + "/" + COMMENTS_CHILD_NODE + "/" + comment.getId(), true);
        childUpdates.put("/" + TIME_LINE + "/" + WALL + "/" + postAuthorKey + "/" + postId + "/" + COMMENTS_CHILD_NODE + "/" + comment.getId(), true);
        for (String friendKey : post.getViewer().keySet()) {
            childUpdates.put("/" + TIME_LINE + "/" + NEWS_FEED + "/" + friendKey + "/" + postId + "/" + COMMENTS_CHILD_NODE + "/" + comment.getId(), true);
        }
        FireBaseDataUtils.getInstance().getFirebaseReference().updateChildren(childUpdates, completionListener);
    }
}
