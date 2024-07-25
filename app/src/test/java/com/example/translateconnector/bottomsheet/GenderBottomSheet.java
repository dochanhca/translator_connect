package com.example.translateconnector.bottomsheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ton on 3/31/18.
 */

public class GenderBottomSheet extends BottomSheetDialogFragment {
    private OnClickGenderOptionListener listener;
    private String selectedGender = "";
    @BindView(R.id.option_male)
    OpenSansBoldTextView optionMale;
    @BindView(R.id.option_female)
    OpenSansBoldTextView optionFemale;
    @BindView(R.id.option_other)
    OpenSansBoldTextView optionOther;

    public interface OnClickGenderOptionListener {
        void onGenderOptionClicked(String genderSelected);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gender_bottom_sheet_option, container, false);
        ButterKnife.bind(this, rootView);
        int colorBlack = ContextCompat.getColor(getContext(), R.color.black);
        int colorBlue = ContextCompat.getColor(getContext(), R.color.medium_blue);
        optionMale.setTextColor(selectedGender.equals(optionMale.getText()) ? colorBlue : colorBlack);
        optionFemale.setTextColor(selectedGender.equals(optionFemale.getText()) ? colorBlue : colorBlack);
        optionOther.setTextColor(selectedGender.equals(optionOther.getText()) ? colorBlue : colorBlack);
        return rootView;
    }

    @OnClick({R.id.option_male, R.id.option_female, R.id.option_other})
    public void onOptionClicked(View view) {
        listener.onGenderOptionClicked(((OpenSansBoldTextView) view).getText().toString());
        dismiss();
    }

    public void setOnClickGenderOptionListener(OnClickGenderOptionListener listener) {
        this.listener = listener;
    }

    public String getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(String selectedGender) {
        this.selectedGender = selectedGender;
    }

}
