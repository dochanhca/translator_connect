package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.customview.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedImageAdapter extends BaseRecyclerAdapter<SelectedImageAdapter.ViewHolder> {

    private Context context;
    private List<Image> images;
    private OnItemClickListener onItemClickListener;

    public SelectedImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_image, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holder.imgDeleteImage.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            images.remove(pos);
            notifyDataSetChanged();
            onItemClickListener.onDeleteImage(pos);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Image image = images.get(position);
        Glide.with(context)
                .load(image.path)
                .error(R.drawable.img_loading_default)
                .placeholder(R.drawable.img_loading_default)
                .thumbnail(0.1f)
                .into(holder.imgSelected);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_selected)
        SquareImageView imgSelected;
        @BindView(R.id.img_delete_image)
        ImageView imgDeleteImage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onDeleteImage(int pos);
    }
}
