package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.FileModel;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<FileModel> photos;

    public ImagePagerAdapter(List<FileModel> photos, Context context) {
        this.photos = photos;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = inflater.inflate(R.layout.item_image_detail, container, false);

        FileModel imageItem = photos.get(position);

        ImageView imgPhoto = convertView.findViewById(R.id.img_origin);

        Glide.with(context)
                .load(imageItem.getUrlFile())
                .error(R.drawable.img_loading_default)
                .placeholder(R.drawable.img_loading_default)
                .thumbnail(0.1f)
                .into(imgPhoto);

        container.addView(convertView, 0);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
