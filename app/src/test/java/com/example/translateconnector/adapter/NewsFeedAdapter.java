package com.example.translateconnector.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.imoktranslator.R;
import com.imoktranslator.customview.ImageBoxLayout;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.firebase.model.Post;
import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.PlayerManager;
import com.imoktranslator.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.PostViewHolder> {
    private Activity activity;
    private String userKey;
    private List<Post> postList;
    private Map<String, Post> postMap = new HashMap<>();
    private OnPostActionListener listener;
    private PlayerManager playerManager;
    private String currentUserKey;

    public NewsFeedAdapter(Activity activity, List<Post> postList, OnPostActionListener onPostActionListener,
                           String userKey) {
        this.postList = postList;
        this.activity = activity;
        this.listener = onPostActionListener;
        this.userKey = userKey;
        this.currentUserKey = LocalSharedPreferences.getInstance(activity).getKeyUser();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_newfeeds, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        Glide.with(activity)
                .load(post.getAuthor().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(holder.avatar);

        holder.name.setText(post.getAuthor().getName());
        holder.tvMessage.setText(post.getMessage());
        holder.tvLikeCount.setText(String.valueOf(post.getLikes().size()));
        holder.tvCommentCount.setText(String.valueOf(post.getComments().size()));
        holder.btLike.setImageResource(post.getLikes().containsKey(userKey) ? R.drawable.ic_like : R.drawable.ic_un_like);
        holder.btLike.setOnClickListener(v -> listener.onLikeClicked(post));
        holder.tvDate.setText(convertDate(post));

        holder.txtViewMore.post(() -> holder.txtViewMore.setVisibility(Utils.isTextEllipsized(holder.tvMessage) ? View.VISIBLE : View.GONE));
        holder.txtViewMore.setOnClickListener(v -> listener.onViewPostDetail(post));

        holder.layoutComment.setOnClickListener(v -> listener.onViewPostDetail(post));
        holder.btComment.setOnClickListener(v -> listener.onViewAllComments(post));
        holder.imgMoreActions.setOnClickListener(v -> showPopup(holder.imgMoreActions, post));

        if (!post.getFileModels().isEmpty()) {
            if (post.getFileModels().get(0).getType().equals(FireBaseDataUtils.TYPE_IMAGE)) {
                holder.imageBoxLayout.setVisibility(View.VISIBLE);
                holder.imageBoxLayout.setUrls(getUrls(post.getFileModels()), selectedPos -> {
                    // Show BaseImageUploadView images screen
                    listener.onViewImage(post, selectedPos);
                });
            } else {
                loadMedia(holder, post.getFileModels().get(0));
            }
        } else {
            holder.imageBoxLayout.setVisibility(View.GONE);
            holder.layoutPlayerView.setVisibility(View.GONE);
        }
        bindPostMode(post, holder.ivPostMode);
        holder.imgMoreActions.setOnClickListener(v -> showPopup(holder.imgMoreActions, post));
        holder.avatar.setOnClickListener(v -> listener.onAvatarClick(post));
    }

    private void bindPostMode(Post post, ImageView ivPostMode) {
        if (!TextUtils.isEmpty(post.getMode())) {
            ivPostMode.setVisibility(View.VISIBLE);
            if (post.getMode().equals(Constants.PUBLIC_MODE)) {
                ivPostMode.setImageResource(R.drawable.ic_public);
            } else if (post.getMode().equals(Constants.FRIENDS_MODE)) {
                ivPostMode.setImageResource(R.drawable.ic_friends);
            } else if (post.getMode().equals(Constants.PRIVATE_MODE)) {
                ivPostMode.setImageResource(R.drawable.ic_only_me);
            }
        } else {
            ivPostMode.setVisibility(View.GONE);
        }

        ivPostMode.setOnClickListener(v -> listener.onEditMode(post));
    }

    private List<String> getUrls(List<FileModel> images) {
        List<String> urls = new ArrayList<>();
        for (FileModel file : images) {
            urls.add(file.getUrlFile());
        }
        return urls;
    }

    private CharSequence convertDate(Post post) {
        return DateTimeUtils.convertTimestamp(activity, String.valueOf(post.getTimestamp()));
    }

    private void loadMedia(PostViewHolder holder, FileModel fileModel) {
        holder.layoutPlayerView.setVisibility(View.VISIBLE);
        if (playerManager == null) {
            playerManager = PlayerManager.getPlayerManagerInstance();
        }
        adjustPlayView(holder, fileModel.getType());
        playerManager.init(activity, holder.playerView, holder.layoutPlayerView,
                fileModel.getUrlFile(), null);
        playerManager.setPayWhenReady(false);
    }

    private void adjustPlayView(PostViewHolder holder, String type) {
        int pixels = (int) activity.getResources().getDimension(type.equals(FireBaseDataUtils.TYPE_VIDEO)
                ? R.dimen.video_view_h : R.dimen.audio_view_h);

        ViewGroup.LayoutParams params = holder.layoutPlayerView.getLayoutParams();
        params.height = pixels;
        holder.layoutPlayerView.setLayoutParams(params);
    }

    private void showPopup(View view, Post post) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(post.getAuthor().getKey().equals(currentUserKey) ?
                R.menu.menu_post_options_owner : R.menu.menu_post_options_visitor);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.post_edit:
                    listener.onEditPost(post);
                    break;
                case R.id.post_share_fb:
                    listener.onShareFbPost(post);
                    break;
                case R.id.post_delete:
                    listener.onDeletePost(post);
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public synchronized void add(int position, Post item) {
        postList.add(position, item);
        postMap.put(item.getId(), item);
        notifyItemInserted(position);
    }

    public synchronized void remove(String postId) {
        Post item = postMap.get(postId);
        int position = postList.indexOf(item);

        if (item != null && position != -1) {
            postList.remove(item);
            postMap.remove(postId);
            notifyItemRemoved(position);
        }
    }

    public synchronized void updateItem(Post post) {
        Post targetPost = postMap.get(post.getId());
        int position = postList.indexOf(targetPost);

        postList.set(position, post);
        postMap.put(post.getId(), post);
        notifyItemChanged(position);
    }

    public boolean isContainThisPost(String postId) {
        Post post = postMap.get(postId);
        int position = postList.indexOf(post);
        return post != null && position != -1;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_avatar)
        CircleImageView avatar;
        @BindView(R.id.contact_name)
        OpenSansBoldTextView name;
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.tv_like_count)
        OpenSansTextView tvLikeCount;
        @BindView(R.id.bt_like)
        ImageView btLike;
        @BindView(R.id.tv_date)
        OpenSansTextView tvDate;
        @BindView(R.id.layout_comment)
        LinearLayout layoutComment;
        @BindView(R.id.bt_comment)
        ImageView btComment;
        @BindView(R.id.tv_comment_count)
        OpenSansTextView tvCommentCount;
        @BindView(R.id.image_box)
        ImageBoxLayout imageBoxLayout;
        @BindView(R.id.txt_view_more)
        TextView txtViewMore;
        @BindView(R.id.img_more_actions)
        ImageView imgMoreActions;
        @BindView(R.id.iv_post_mode)
        ImageView ivPostMode;
        @BindView(R.id.layout_player_view)
        ViewGroup layoutPlayerView;
        @BindView(R.id.player_view)
        PlayerView playerView;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPostActionListener {
        void onLikeClicked(Post post);

        void onEditPost(Post post);

        void onShareFbPost(Post post);

        void onDeletePost(Post post);

        void onViewAllComments(Post post);

        void onViewPostDetail(Post post);

        void onEditMode(Post post);

        void onViewImage(Post post, int selectedPos);

        void onAvatarClick(Post post);
    }
}
