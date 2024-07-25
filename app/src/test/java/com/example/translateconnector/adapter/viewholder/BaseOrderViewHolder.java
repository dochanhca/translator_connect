package com.example.translateconnector.adapter.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.adapter.OrderAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SwipeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseOrderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.txt_order_name)
    public OpenSansBoldTextView txtOrderName;
    @BindView(R.id.txt_date_created)
    public OpenSansTextView txtDateCreated;
    @BindView(R.id.txt_price)
    public OpenSansBoldTextView txtPrice;
    @BindView(R.id.txt_place_label)
    public TextView txtPlaceLabel;
    @BindView(R.id.txt_place)
    public OpenSansBoldTextView txtPlace;
    @BindView(R.id.txt_language_label)
    public TextView txtLanguageLabel;
    @BindView(R.id.txt_language)
    public OpenSansBoldTextView txtLanguage;
    @BindView(R.id.txt_price_label)
    public TextView txtPriceLabel;
    @BindView(R.id.txt_order_status)
    public TextView txtOrderStatus;
    @Nullable
    @BindView(R.id.swipe_layout)
    public SwipeLayout swipeLayout;
    @Nullable
    @BindView(R.id.layout_order_info)
    public ViewGroup layoutOrderInfo;

    protected OrderAdapter.OnOrderClickListener onOrderClickListener;

    public BaseOrderViewHolder(View view, OrderAdapter.OnOrderClickListener onOrderClickListener) {
        super(view);
        this.onOrderClickListener = onOrderClickListener;
        ButterKnife.bind(this, view);
    }
}
