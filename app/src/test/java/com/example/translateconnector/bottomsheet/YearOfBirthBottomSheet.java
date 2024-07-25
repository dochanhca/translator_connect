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
import com.imoktranslator.adapter.YearOfBirthAdapter;
import com.imoktranslator.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/1/18.
 */

public class YearOfBirthBottomSheet extends BottomSheetDialogFragment {
    private static final int MIN_YEAR = 1950;
    @BindView(R.id.rv_year_of_birth)
    RecyclerView rvYear;

    private int selectedYear = -1;
    private List<Integer> listYear = new ArrayList<>();
    private OnYearClickListener listener;

    public interface OnYearClickListener {
        void onYearClicked(String year);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.year_of_birth_bottom_sheet_option, container, false);
        ButterKnife.bind(this, rootView);
        initListYear();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvYear.setLayoutManager(linearLayoutManager);

        YearOfBirthAdapter adapter = new YearOfBirthAdapter(getContext(), selectedYear, listYear);
        adapter.setOnItemClickListener((view, position) -> {
            if (listener != null) {
                listener.onYearClicked(String.valueOf(listYear.get(position)));
            }
            dismiss();
        });
        rvYear.setAdapter(adapter);
        linearLayoutManager.scrollToPositionWithOffset(listYear.indexOf(selectedYear), (int) Utils.pxFromDp(getContext(), 145));
        return rootView;
    }

    private void initListYear() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= MIN_YEAR; i--) {
            listYear.add(i);
        }
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public OnYearClickListener getOnYearClickListener() {
        return listener;
    }

    public void setOnYearClickListener(OnYearClickListener onYearClickListener) {
        this.listener = onYearClickListener;
    }
}
