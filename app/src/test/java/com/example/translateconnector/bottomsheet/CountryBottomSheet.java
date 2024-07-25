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
import com.imoktranslator.adapter.CountryAdapter;
import com.imoktranslator.model.LocationModel;
import com.imoktranslator.utils.LocationConstantCN;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/2/18.
 */

public class CountryBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.rv_country)
    RecyclerView rvCountry;

    private OnCountryClickListener listener;
    private List<LocationModel> locationList;
    private String selectedCountry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.country_bottom_sheet_option, container, false);
        ButterKnife.bind(this, rootView);
        locationList = LocationConstantCN.getInstance().getLocationData().getLocations();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCountry.setLayoutManager(linearLayoutManager);

        CountryAdapter adapter = new CountryAdapter(getContext(), locationList, selectedCountry);
        adapter.setOnItemClickListener((view, position) -> {
            if (listener != null) {
                String newSelection = locationList.get(position).getCountry();
                if (!newSelection.equals(selectedCountry)) {
                    listener.onCountryClicked(newSelection);
                }
            }
            dismiss();
        });
        rvCountry.setAdapter(adapter);
        linearLayoutManager.scrollToPositionWithOffset(indexOf(selectedCountry), (int) Utils.pxFromDp(getContext(), 145));

        return rootView;
    }

    private int indexOf(String selectedCountry) {
        int index = 0;
        for (LocationModel location : locationList) {
            if (location.getCountry().equals(selectedCountry)) {
                index = locationList.indexOf(location);
                break;
            }
        }
        return index;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public OnCountryClickListener getOnCountryClickListener() {
        return listener;
    }

    public void setOnCountryClickListener(OnCountryClickListener onCountryClickListener) {
        this.listener = onCountryClickListener;
    }

    public interface OnCountryClickListener {
        void onCountryClicked(String countryName);
    }
}
