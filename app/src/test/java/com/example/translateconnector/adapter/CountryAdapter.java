package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.model.LocationModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/2/18.
 */

public class CountryAdapter extends BaseRecyclerAdapter<CountryAdapter.CountryViewHolder> {
    private Context context;
    private List<LocationModel> locationList;
    private String selectedCountry;
    private int unSelectedColor;
    private int selectedColor;

    public CountryAdapter(Context context, List<LocationModel> locationList, String selectedCountry) {
        this.context = context;
        this.locationList = locationList;
        this.selectedCountry = selectedCountry;
        unSelectedColor = ContextCompat.getColor(context, R.color.black);
        selectedColor = ContextCompat.getColor(context, R.color.medium_blue);
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        LocationModel locationModel = locationList.get(position);
        holder.tvCountry.setText(locationModel.getCountry());
        holder.tvCountry.setTextColor(locationModel.getCountry().equals(selectedCountry) ? selectedColor : unSelectedColor);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    static class CountryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_country)
        OpenSansBoldTextView tvCountry;

        public CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
