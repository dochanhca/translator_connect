package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/29/18.
 */

public class SideMenuAdapter extends BaseRecyclerAdapter<SideMenuAdapter.ViewHolder> {

    private Context context;
    private List<String> menuItems;

    public SideMenuAdapter(Context context, List<String> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.txtMenuTitle.setText(menuItems.get(position));
        if (menuItems.get(position).equals(context.getString(R.string.MH21_011))) {
            holder.txtMenuTitle.setTextColor(ContextCompat.getColor(context, R.color.salmon_pink));
            holder.ivArrow.setVisibility(View.GONE);
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_menu_title)
        OpenSansSemiBoldTextView txtMenuTitle;
        @BindView(R.id.iv_arrow)
        ImageView ivArrow;
        @BindView(R.id.line)
        View line;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
