package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListCertificateAdapter extends BaseRecyclerAdapter<ListCertificateAdapter.ViewHolder> {

    private List<String> certificates;
    private Context context;

    public ListCertificateAdapter(List<String> certificates, Context context) {
        this.certificates = certificates;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.txtCertificate.setText(certificates.get(position));
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    public void setData(List<String> certificates) {
        this.certificates.clear();
        this.certificates.addAll(certificates);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_certificate)
        OpenSansTextView txtCertificate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
