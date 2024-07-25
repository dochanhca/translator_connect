package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.model.ProvinceModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/3/18.
 */

public class ProvinceAdapter extends BaseRecyclerAdapter<ProvinceAdapter.ProvinceViewHolder> {
    private Context context;
    private List<ProvinceModel> provinceList;
    private String selectedProvince;
    private int unSelectedColor;
    private int selectedColor;


    public ProvinceAdapter(Context context, List<ProvinceModel> provinceList, String selectedProvince) {
        this.context = context;
        this.provinceList = provinceList;
        this.selectedProvince = selectedProvince;
        unSelectedColor = ContextCompat.getColor(context, R.color.black);
        selectedColor = ContextCompat.getColor(context, R.color.medium_blue);
    }

    @Override
    public ProvinceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_province, parent, false);
        return new ProvinceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProvinceViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ProvinceModel province = provinceList.get(position);
        holder.tvProvince.setText(province.getName());
        holder.tvProvince.setTextColor(province.getName().equals(selectedProvince) ? selectedColor : unSelectedColor);
    }

    @Override
    public int getItemCount() {
        return provinceList.size();
    }

    static class ProvinceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_province)
        OpenSansBoldTextView tvProvince;

        public ProvinceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
