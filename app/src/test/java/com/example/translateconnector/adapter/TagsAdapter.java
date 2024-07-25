package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.imoktranslator.R;
import com.imoktranslator.customview.TagView;

import java.util.List;

/**
 * Created by ton on 4/7/18.
 */

public class TagsAdapter extends ArrayAdapter<String> {
    private List<String> dataSet;
    private Context context;
    private int layoutId;
    private OnDeleteTagClickListener listener;

    public TagsAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, OnDeleteTagClickListener listener) {
        super(context, resource, objects);
        this.dataSet = objects;
        this.context = context;
        this.layoutId = resource;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        String item = dataSet.get(position);
        TagView tagView = view.findViewById(R.id.tag_name);
        tagView.setTagName(item);
        tagView.getDeleteButton().setOnClickListener(view1 -> listener.onDeleteTagClicked(position));
        return view;
    }

    public interface OnDeleteTagClickListener {
        void onDeleteTagClicked(int position);
    }
}
