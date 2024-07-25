package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.firebase.model.Comment;
import com.imoktranslator.utils.DateTimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirebaseRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {

    private Context context;
    private OnCommentClickListener onCommentClickListener;

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public CommentAdapter(@NonNull FirebaseRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        Glide.with(context)
                .load(model.getAuthor().getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(holder.ivAvatar);

        holder.imgComment.setVisibility(View.GONE);
        holder.tvName.setText(model.getAuthor().getName());
        holder.tvMessage.setVisibility(TextUtils.isEmpty(model.getMessage()) ? View.GONE : View.VISIBLE);
        holder.tvMessage.setText(model.getMessage());
        holder.tvDate.setText(convertDate(model));
        if (model.getFile() != null) {
            holder.imgComment.setOnClickListener(v -> {
                onCommentClickListener.onImageClick(model);
            });

            holder.imgComment.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(model.getFile().getUrlFile())
                    .error(R.drawable.img_loading_default)
                    .placeholder(R.drawable.img_loading_default)
                    .thumbnail(0.1f)
                    .into(holder.imgComment);
        }
    }

    private CharSequence convertDate(Comment comment) {
        return DateTimeUtils.convertTimestamp(context, String.valueOf(comment.getTimestamp()));
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.contact_name)
        OpenSansBoldTextView tvName;
        @BindView(R.id.tv_message)
        OpenSansTextView tvMessage;
        @BindView(R.id.tv_date)
        OpenSansTextView tvDate;
        @BindView(R.id.img_comment)
        ImageView imgComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnCommentClickListener {
        void onImageClick(Comment comment);
    }
}
