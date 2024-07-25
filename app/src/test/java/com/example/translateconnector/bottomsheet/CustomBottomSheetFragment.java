package com.example.translateconnector.bottomsheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.adapter.BottomSheetAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ton on 4/7/18.
 */

public class CustomBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TITLE = "TITLE";

    @BindView(R.id.rv_options)
    RecyclerView rvOptions;
    @BindView(R.id.txt_title)
    OpenSansBoldTextView txtTitle;

    private OnOptionClickListener listener;
    private List<String> options;
    private BottomSheetAdapter adapter;
    private int selectedPosition;

    public static CustomBottomSheetFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        CustomBottomSheetFragment fragment = new CustomBottomSheetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_custom_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOptions.setLayoutManager(linearLayoutManager);

        adapter = new BottomSheetAdapter(getContext(), options, selectedPosition);
        adapter.setOnItemClickListener((view, position) -> {
            if (listener != null) {
                listener.onOptionClicked(position);
            }
            dismiss();
        });
        rvOptions.setAdapter(adapter);
        linearLayoutManager.scrollToPositionWithOffset(selectedPosition, (int) Utils.pxFromDp(getContext(), 145));

        if (getArguments() != null) {
            String title = getArguments().getString(TITLE);
            setTxtTitle(title);
        }

        return rootView;
    }

    private void setTxtTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            txtTitle.setVisibility(View.GONE);
        }
       txtTitle.setText(title);
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public OnOptionClickListener getListener() {
        return listener;
    }

    public void setListener(OnOptionClickListener listener) {
        this.listener = listener;
    }

    public interface OnOptionClickListener {
        void onOptionClicked(int position);
    }
}
