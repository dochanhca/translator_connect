package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.customview.SelectionView;

import java.util.List;

public class CommonSpinnerAdapter extends ArrayAdapter {

    private List<String> data;
    private LayoutInflater inflater;
    private int selectedPosition = 0;
    private String hint;

    public CommonSpinnerAdapter(Context context, List<String> data, String hint) {
        super(context, -1, data);
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.hint = hint;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_common_spinner, parent, false);

        SelectionView selectionView = convertView.findViewById(R.id.selection);
        selectionView.setHint(hint);
        selectionView.setSelectionValue(data.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_common_spinner_dropdown, parent, false);
        return getView(position, convertView);
    }

    private View getView(int position, View convertView) {
        TextView txtBrand = convertView.findViewById(R.id.txt_value);
        txtBrand.setText(data.get(position));

        if (position == selectedPosition) {
            txtBrand.setTextColor(getContext().getResources().getColor(R.color.dark_sky_blue));
        } else {
            txtBrand.setTextColor(getContext().getResources().getColor(R.color.text_brown));
        }

        return convertView;
    }

    public void setItemSelected(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
