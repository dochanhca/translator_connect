package com.example.translateconnector.bottomsheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.ProvinceAdapter;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/3/18.
 */

public class ProvinceBottomSheet extends BottomSheetDialogFragment {
    @BindView(R.id.rv_provinces)
    RecyclerView rvProvinces;

    private List<ProvinceModel> provinceList;
    private OnProvinceClickListener listener;
    private String selectedProvince;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.provinces_bottom_sheet_option, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProvinces.setLayoutManager(linearLayoutManager);

        ProvinceAdapter adapter = new ProvinceAdapter(getContext(), provinceList, selectedProvince);
        adapter.setOnItemClickListener((view, position) -> {
            if (listener != null) {
                String newSelection = provinceList.get(position).getName();
                if (!newSelection.equals(selectedProvince)) {
                    listener.onProvinceClicked(newSelection);
                }
            }
            dismiss();
        });
        rvProvinces.setAdapter(adapter);
        linearLayoutManager.scrollToPositionWithOffset(indexOf(selectedProvince), (int) Utils.pxFromDp(getContext(), 145));

        return rootView;
    }

    private int indexOf(String selectedProvince) {
        int index = 0;
        for (ProvinceModel province : provinceList) {
            if (province.getName().equals(selectedProvince)) {
                index = provinceList.indexOf(province);
                break;
            }
        }
        return index;
    }

    public List<ProvinceModel> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceModel> provinceList) {
        this.provinceList = provinceList;
    }

    public interface OnProvinceClickListener {
        void onProvinceClicked(String provinceName);
    }

    public OnProvinceClickListener getOnProvinceClickListener() {
        return listener;
    }

    public void setOnProvinceClickListener(OnProvinceClickListener onProvinceClickListener) {
        this.listener = onProvinceClickListener;
    }

    public String getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(String selectedProvince) {
        this.selectedProvince = selectedProvince;
    }
}
