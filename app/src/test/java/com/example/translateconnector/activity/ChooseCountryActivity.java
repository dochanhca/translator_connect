package com.example.translateconnector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.imoktranslator.R;
import com.imoktranslator.adapter.CountryPhoneAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.model.PhoneCodeItem;
import com.imoktranslator.presenter.ChooseCountryPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ducpv on 3/25/18.
 */

public class ChooseCountryActivity extends BaseActivity implements ChooseCountryPresenter.ChooseCountryView, HeaderView.BackButtonClickListener {

    public static final String SELECTED_COUNTRY = "SELECTED_COUNTRY";

    @BindView(R.id.header)
    HeaderView headerView;
    @BindView(R.id.edt_search_country)
    OpenSansEditText edtSearchCountry;
    @BindView(R.id.rcv_country_phone)
    RecyclerView rcvCountryPhone;

    private CountryPhoneAdapter countryPhoneAdapter;
    private ChooseCountryPresenter presenter;

    public static void startAcitivity(BaseActivity activity, int requestCode) {
        Intent intent = new Intent(activity, ChooseCountryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_country;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH05_007));

        countryPhoneAdapter = new CountryPhoneAdapter(new ArrayList<>(), getApplicationContext());
        countryPhoneAdapter.setOnItemClickListener((view, position) ->
                onCountrySelected(countryPhoneAdapter.getData().get(position)));

        rcvCountryPhone.setAdapter(countryPhoneAdapter);
        rcvCountryPhone.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        presenter = new ChooseCountryPresenter(this, this);
        presenter.getPhoneCodesFromAsserts();

        initSearchTextChanged();
    }

    private void onCountrySelected(PhoneCodeItem phoneCodeItem) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_COUNTRY, phoneCodeItem);
        resultIntent.putExtras(bundle);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void initSearchTextChanged() {
        edtSearchCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                countryPhoneAdapter.filterCountry(editable.toString());
            }
        });
    }

    @Override
    public void getPhoneCodes(List<PhoneCodeItem> phoneCodeItems) {
        countryPhoneAdapter.setData(phoneCodeItems);
    }

    @Override
    public void getPhoneCodesError(String errMessage) {

    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }
}
