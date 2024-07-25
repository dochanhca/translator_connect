package com.example.translateconnector.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;

import butterknife.BindView;

public class TradingOrderViewHolder extends BaseOrderViewHolder {

    @BindView(R.id.layout_cancel)
    ViewGroup layoutCancel;
    @BindView(R.id.layout_end_early)
    ViewGroup layoutEndEarly;

    public TradingOrderViewHolder(View view, OrderAdapter.OnOrderClickListener onOrderClickListener) {
        super(view, onOrderClickListener);
        layoutCancel.setOnClickListener(v -> onOrderClickListener.onOrderCancelClick(getAdapterPosition()));
        layoutEndEarly.setOnClickListener(v -> onOrderClickListener.onOrderEndEarlyClick(getAdapterPosition()));
    }
}
