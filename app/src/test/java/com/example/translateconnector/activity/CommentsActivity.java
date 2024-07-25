package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.imoktranslator.R;
import com.imoktranslator.adapter.CommentAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.firebase.model.Comment;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.utils.FireBaseDataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommentsActivity extends BaseActivity implements HeaderView.BackButtonClickListener {

    private static final String KEY_POST_ID = "key_post_id";
    @BindView(R.id.header)
    HeaderView headerView;
    @BindView(R.id.rv_comments)
    RecyclerView rvComment;
    private CommentAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comments_activity;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH99_006));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String postId = getIntent().getStringExtra(KEY_POST_ID);
        Query query = FireBaseDataUtils.getInstance().getFirebaseReference()
                .child(FireBaseDataUtils.TIME_LINE)
                .child(FireBaseDataUtils.COMMENTS)
                .child(postId);

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();

        adapter = new CommentAdapter(options, this);
        rvComment.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnCommentClickListener(comment -> {
            List<FileModel> fileModels = new ArrayList<>();
            fileModels.add(comment.getFile());
            ImageDetailActivity.showActivity(this, fileModels, 0);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    public static void startActivity(BaseActivity activity, String postId) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra(KEY_POST_ID, postId);
        activity.startActivity(intent);
    }
}
