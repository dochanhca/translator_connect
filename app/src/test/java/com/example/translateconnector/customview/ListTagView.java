package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.imoktranslator.R;
import com.imoktranslator.adapter.TagsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/7/18.
 */

public class ListTagView extends LinearLayout implements TagsAdapter.OnDeleteTagClickListener {
    @BindView(R.id.listView)
    ListViewFixedHeight listView;

    private List<String> dataSet;
    private TagsAdapter adapter;
    private int maxItem;
    private OnListStateListener listener;

    public ListTagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_list_tag_view, this, true);
        ButterKnife.bind(this);
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ListTagView,
                0, 0);
        if (ta != null) {
            maxItem = ta.getInt(R.styleable.ListTagView_maxItem, 10);
            ta.recycle();
        }

        dataSet = new ArrayList<>();
        adapter = new TagsAdapter(getContext(), R.layout.tag_item, dataSet, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDeleteTagClicked(int position) {
        String tag = dataSet.get(position);
        dataSet.remove(position);
        adapter.notifyDataSetChanged();
        if (listener != null) {
            listener.onRemoved(tag);
        }
    }

    public void setOnListStateListener(OnListStateListener onListStateListener) {
        this.listener = onListStateListener;
    }

    public void add(String tag) {
        if (!isFullSlot()) {
            dataSet.add(tag);
            adapter.notifyDataSetChanged();
            if (listener != null) {
                listener.onAdded(tag);
            }
        } else {
            if (listener != null) {
                listener.onFullSlot();
            }
        }
    }

    public boolean isContain(String tag) {
        return dataSet.contains(tag);
    }

    public boolean isEmpty() {
        return dataSet.isEmpty();
    }

    public List<String> getDataSet() {
        return dataSet;
    }

    public boolean isFullSlot() {
        return dataSet.size() >= maxItem;
    }

    public interface OnListStateListener {
        void onAdded(String tagName);

        void onRemoved(String tagName);

        void onFullSlot();
    }
}
