package com.example.translateconnector.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;

import butterknife.BindView;

public class NewOrderViewHolder extends BaseOrderViewHolder {

    @BindView(R.id.layout_hide)
    ViewGroup layoutHide;

    public NewOrderViewHolder(View view, OrderAdapter.OnOrderClickListener onOrderClickListener) {
        super(view, onOrderClickListener);
        layoutHide.setOnClickListener(v -> onOrderClickListener.onHideOrderClick(getAdapterPosition()));
    }
}
