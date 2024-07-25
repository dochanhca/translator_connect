package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/1/18.
 */

public class YearOfBirthAdapter extends BaseRecyclerAdapter<YearOfBirthAdapter.YearViewHolder> {
    private Context context;
    private List<Integer> listYear;
    private int unSelectedColor;
    private int selectedColor;
    private int selectedYear = -1;

    public YearOfBirthAdapter(Context context, int selectedYear, List<Integer> listYear) {
        this.context = context;
        this.selectedYear = selectedYear;
        this.listYear = listYear;
        unSelectedColor = ContextCompat.getColor(context, R.color.black);
        selectedColor = ContextCompat.getColor(context, R.color.medium_blue);

    }

    @Override
    public YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_year, parent, false);
        return new YearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(YearViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.tvYear.setText(String.valueOf(listYear.get(position)));
        holder.tvYear.setTextColor(listYear.get(position) == selectedYear ? selectedColor : unSelectedColor);
    }

    @Override
    public int getItemCount() {
        return listYear.size();
    }

    static class YearViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_year)
        OpenSansBoldTextView tvYear;

        public YearViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
