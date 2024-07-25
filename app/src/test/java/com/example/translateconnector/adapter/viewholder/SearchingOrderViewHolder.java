package com.example.translateconnector.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;

import butterknife.BindView;

public class SearchingOrderViewHolder extends BaseOrderViewHolder {

    @BindView(R.id.layout_cancel)
    ViewGroup layoutCancel;

    public SearchingOrderViewHolder(View view, OrderAdapter.OnOrderClickListener onOrderClickListener) {
        super(view, onOrderClickListener);
        layoutCancel.setOnClickListener(v -> onOrderClickListener.onOrderCancelClick(getAdapterPosition()));
    }
}
