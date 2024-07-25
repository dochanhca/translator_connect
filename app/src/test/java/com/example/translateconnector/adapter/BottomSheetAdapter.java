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
 * Created by ton on 4/2/18.
 */

public class BottomSheetAdapter extends BaseRecyclerAdapter<BottomSheetAdapter.BSViewHolder> {
    private Context context;
    private List<String> data;
    private int selectedPosition;
    private int unSelectedColor;
    private int selectedColor;

    public BottomSheetAdapter(Context context, List<String> data, int selectedPosition) {
        this.context = context;
        this.data = data;
        this.selectedPosition = selectedPosition;
        unSelectedColor = ContextCompat.getColor(context, R.color.black);
        selectedColor = ContextCompat.getColor(context, R.color.medium_blue);
    }

    @Override
    public BSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_row, parent, false);
        return new BSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BSViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String option = data.get(position);
        holder.tvOption.setText(option);
        holder.tvOption.setTextColor(selectedPosition == position ? selectedColor : unSelectedColor);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BSViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_option)
        OpenSansBoldTextView tvOption;

        public BSViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
