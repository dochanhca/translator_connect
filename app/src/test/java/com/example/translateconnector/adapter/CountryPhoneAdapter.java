package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.PhoneCodeItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/25/18.
 */

public class CountryPhoneAdapter extends BaseRecyclerAdapter<CountryPhoneAdapter.ViewHolder> {

    private List<PhoneCodeItem> phoneCodeItems;
    private List<PhoneCodeItem> localPhoneCodeItems;
    private Context context;

    public CountryPhoneAdapter(List<PhoneCodeItem> phoneCodeItems, Context context) {
        this.phoneCodeItems = new ArrayList<>();
        this.localPhoneCodeItems = new ArrayList<>();

        this.phoneCodeItems.addAll(phoneCodeItems);
        this.localPhoneCodeItems.addAll(phoneCodeItems);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country_phone, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PhoneCodeItem item = phoneCodeItems.get(position);
        holder.txtCountryName.setText(item.getCountry());
        holder.txtPhoneCode.setText(item.getCode());
    }

    @Override
    public int getItemCount() {
        return phoneCodeItems.size();
    }

    public void setData(List<PhoneCodeItem> phoneCodeItems) {
        this.phoneCodeItems.clear();
        this.phoneCodeItems.addAll(phoneCodeItems);
        this.localPhoneCodeItems.clear();
        this.localPhoneCodeItems.addAll(phoneCodeItems);
        notifyDataSetChanged();
    }

    public List<PhoneCodeItem> getData() {
        return this.phoneCodeItems;
    }

    public void filterCountry(String text) {
        phoneCodeItems.clear();
        if (!text.isEmpty()) {
            for (PhoneCodeItem item : localPhoneCodeItems) {
                if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                    phoneCodeItems.add(item);
                }
            }
        } else {
            phoneCodeItems.addAll(localPhoneCodeItems);
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_country_name)
        OpenSansBoldTextView txtCountryName;
        @BindView(R.id.txt_phone_code)
        OpenSansTextView txtPhoneCode;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
