package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;

/**
 * Created by ducpv on 3/25/18.
 */

public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private RecyclerViewClickListener.OnItemClickListener itemClickListener;
    private RecyclerViewClickListener.OnItemLongClickListener itemLongClickListener;

    public void setOnItemClickListener(
            @NonNull RecyclerViewClickListener.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(
            @NonNull RecyclerViewClickListener.OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(view -> itemClickListener.OnItemClick(view, position));
        }
        if (itemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(view -> {
                itemLongClickListener.OnItemLongClick(view, position);
                return true;
            });
        }
    }

    protected void loadAvatar(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .error(R.drawable.img_default_avatar)
                .into(view);
    }
}
