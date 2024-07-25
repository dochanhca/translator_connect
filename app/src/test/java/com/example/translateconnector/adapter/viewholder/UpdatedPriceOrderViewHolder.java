package com.example.translateconnector.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;

import butterknife.BindView;

public class UpdatedPriceOrderViewHolder extends BaseOrderViewHolder {

    @BindView(R.id.layout_delete)
    ViewGroup layoutDelete;

    public UpdatedPriceOrderViewHolder(View view, OrderAdapter.OnOrderClickListener onOrderClickListener) {
        super(view, onOrderClickListener);
        layoutDelete.setOnClickListener(v -> onOrderClickListener.onHideOrderClick(getAdapterPosition()));
    }
}
