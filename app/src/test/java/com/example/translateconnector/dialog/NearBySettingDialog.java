package com.example.translateconnector.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.adapter.CommonSpinnerAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.NearBySettingItem;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NearBySettingDialog extends BaseDialog {

    @BindView(R.id.txt_message)
    OpenSansTextView txtMessage;
    @BindView(R.id.txt_gender_label)
    OpenSansBoldTextView txtGenderLabel;
    @BindView(R.id.txt_age_label)
    OpenSansBoldTextView txtAgeLabel;
    @BindView(R.id.txt_to_label)
    OpenSansBoldTextView txtToLabel;
    @BindView(R.id.txt_ok)
    OpenSansBoldTextView txtOk;
    @BindView(R.id.txt_cancel)
    OpenSansBoldTextView txtCancel;
    @BindView(R.id.txt_gender_error)
    TextView txtGenderError;
    @BindView(R.id.txt_age_error)
    TextView txtAgeError;
    @BindView(R.id.spinner_gender)
    AppCompatSpinner spinnerGender;
    @BindView(R.id.spinner_from_age)
    AppCompatSpinner spinnerFromAge;
    @BindView(R.id.spinner_to_age)
    AppCompatSpinner spinnerToAge;

    private List<String> genders;
    private List<String> ages;

    private CommonSpinnerAdapter genderAdapter;
    private CommonSpinnerAdapter fromAgeAdapter;
    private CommonSpinnerAdapter toAgeAdapter;

    private NearBySettingItem nearBySettingItem;

    private NearBySettingDialogClickListener nearBySettingDialogClickListener;

    public static void showDialog(FragmentManager fragmentManager,
                                  NearBySettingDialogClickListener nearBySettingDialogClickListener) {
        NearBySettingDialog nearBySettingDialog = new NearBySettingDialog();
        nearBySettingDialog.nearBySettingDialogClickListener = nearBySettingDialogClickListener;
        nearBySettingDialog.show(fragmentManager, NearBySettingDialog.class.getSimpleName());
    }

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_near_by_setting;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        initViews();

        nearBySettingItem = LocalSharedPreferences.getInstance(getActivity()).getNearBySetting();
        if (nearBySettingItem != null) {
            genderAdapter.setItemSelected(nearBySettingItem.getGender() - 1);
            spinnerGender.setSelection(genderAdapter.getSelectedPosition());

            fromAgeAdapter.setItemSelected(ages.indexOf(String.valueOf(nearBySettingItem.getFromAge())));
            spinnerFromAge.setSelection(fromAgeAdapter.getSelectedPosition());

            toAgeAdapter.setItemSelected(ages.indexOf(String.valueOf(nearBySettingItem.getToAge())));
            spinnerToAge.setSelection(toAgeAdapter.getSelectedPosition());
        } else {
            toAgeAdapter.setItemSelected(ages.size() - 1);
            spinnerToAge.setSelection(toAgeAdapter.getSelectedPosition());
            genderAdapter.setItemSelected(genders.size() - 1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        int width = (int) (Utils.getScreenWidth(getContext()) * 0.9);
        setupDialog(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initViews() {
        //Set text UpperCase programmatically because TextAllCaps = true not working
        txtGenderLabel.setText(getString(R.string.MH04_010).toUpperCase());
        txtAgeLabel.setText(getString(R.string.MH49_004).toUpperCase());

        txtGenderLabel.append(":");
        txtAgeLabel.append(":");
        txtToLabel.append(":");

        genders = Arrays.asList(getResources().getStringArray(R.array.arr_gender_order));
        ages = getAges();

        initSpinnerGender();
        initSpinnerFromAge();
        initSpinnerToAge();
    }

    private void initSpinnerToAge() {
        toAgeAdapter = new CommonSpinnerAdapter(getActivity().getApplicationContext(),
                ages, getString(R.string.MH49_004));
        spinnerToAge.setAdapter(toAgeAdapter);
        spinnerToAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int toAge = Integer.valueOf(ages.get(position));
                int fromAge = Integer.valueOf(ages.get(fromAgeAdapter.getSelectedPosition()));
                if (toAge < fromAge) {
                    toAgeAdapter.setItemSelected(fromAgeAdapter.getSelectedPosition());
                    spinnerToAge.setSelection(toAgeAdapter.getSelectedPosition());
                } else {
                    toAgeAdapter.setItemSelected(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerFromAge() {
        fromAgeAdapter = new CommonSpinnerAdapter(getActivity().getApplicationContext(),
                ages, getString(R.string.MH49_004));
        spinnerFromAge.setAdapter(fromAgeAdapter);
        spinnerFromAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int fromAge = Integer.valueOf(ages.get(position));
                int toAge = Integer.valueOf(ages.get(toAgeAdapter.getSelectedPosition()));
                if (toAge < fromAge) {
                    fromAgeAdapter.setItemSelected(toAgeAdapter.getSelectedPosition());
                    spinnerFromAge.setSelection(fromAgeAdapter.getSelectedPosition());
                } else {
                    fromAgeAdapter.setItemSelected(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerGender() {
        genderAdapter = new CommonSpinnerAdapter(getActivity().getApplicationContext(),
                genders, getString(R.string.MH11_018));
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderAdapter.setItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<String> getAges() {
        List<String> ages = new ArrayList<>();
        for (int i = 15; i <= 60; i++) {
            ages.add(String.valueOf(i));
        }
        return ages;
    }

    @OnClick({R.id.txt_ok, R.id.txt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:
                saveNearBySetting();
                dismiss();
                break;
            case R.id.txt_cancel:
                nearBySettingDialogClickListener.onCancelClickListener();
                dismiss();
                break;
        }
    }

    private void saveNearBySetting() {
        int fromAge = Integer.parseInt(ages.get(fromAgeAdapter.getSelectedPosition()));
        int toAge = Integer.parseInt(ages.get(toAgeAdapter.getSelectedPosition()));
        NearBySettingItem nearBySettingItem = new NearBySettingItem(genderAdapter.getSelectedPosition() + 1,
                fromAge, toAge);
        LocalSharedPreferences.getInstance(getActivity()).saveNearBySetting(nearBySettingItem);
        nearBySettingDialogClickListener.onOkClickListener();
    }

    public interface NearBySettingDialogClickListener {
        void onOkClickListener();

        void onCancelClickListener();
    }
}
