package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttachImageAdapter extends BaseRecyclerAdapter<AttachImageAdapter.AttachFileViewHolder> {
    private Context context;
    private List<Image> imageList;
    private OnImageClickListener onImageClickListener;

    public AttachImageAdapter(Context context, List<Image> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    public AttachFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_item, parent, false);
        return new AttachFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachFileViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Image image = imageList.get(position);
        Glide.with(context).load(image.path).fitCenter().into(holder.iv_image);
        holder.iv_delete.setOnClickListener(view -> {
            imageList.remove(position);
            notifyDataSetChanged();
            onImageClickListener.onDeleteImage();
        });
        if (image.id == -1) {
            holder.iv_delete.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    static class AttachFileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.iv_x)
        ImageView iv_delete;

        public AttachFileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnImageClickListener {
        void onDeleteImage();
    }
}
