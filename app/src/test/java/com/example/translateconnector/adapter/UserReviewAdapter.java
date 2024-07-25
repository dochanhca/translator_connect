package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.ReviewContent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserReviewAdapter extends BaseRecyclerAdapter<UserReviewAdapter.ViewHolder> {

    private List<ReviewContent> userReviews;
    private Context context;

    public UserReviewAdapter(List<ReviewContent> userReviews, Context context) {
        this.userReviews = userReviews;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_review, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ReviewContent item = userReviews.get(position);

        holder.txtReview.setText(item.getContent());
        Glide.with(context)
                .load(item.getAvatar())
                .error(R.drawable.img_avatar_default)
                .into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return userReviews.size();
    }

    public void setData(List<ReviewContent> userReviews) {
        this.userReviews.clear();
        this.userReviews.addAll(userReviews);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_avatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_review)
        OpenSansTextView txtReview;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
