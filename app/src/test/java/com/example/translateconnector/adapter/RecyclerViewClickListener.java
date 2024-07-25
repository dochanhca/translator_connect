package com.example.translateconnector.adapter;

import android.view.View;

/**
 * Created by ducpv on 3/25/18.
 */

public class RecyclerViewClickListener {
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void OnItemLongClick(View view, int position);
    }
}
