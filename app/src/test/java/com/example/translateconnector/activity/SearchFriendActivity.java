package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.InputType;
import android.view.View;

import com.imoktranslator.R;
import com.imoktranslator.bottomsheet.CountryBottomSheet;
import com.imoktranslator.bottomsheet.ProvinceBottomSheet;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PhoneCodeItem;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.utils.LocationConstantCN;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchFriendActivity extends BaseActivity implements HeaderView.BackButtonClickListener {

    private static final int REQUEST_SELECT_PHONE_CODE = 11;

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.txt_find_near_by_label)
    OpenSansBoldTextView txtFindNearByLabel;
    @BindView(R.id.txt_find_by_phone_label)
    OpenSansBoldTextView txtFindByPhoneLabel;
    @BindView(R.id.txt_phone_code_number)
    OpenSansTextView txtPhoneCodeNumber;
    @BindView(R.id.edt_phone_number)
    TextFieldView edtPhoneNumber;
    @BindView(R.id.txt_find_by_address)
    OpenSansBoldTextView txtFindByAddress;
    @BindView(R.id.selection_country)
    SelectionView selectionCountry;
    @BindView(R.id.selection_city)
    SelectionView selectionCity;
    @BindView(R.id.txt_find_friend)
    OpenSansBoldTextView txtFindFriend;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_friend;
    }

    @Override
    protected void initViews() {
        header.setCallback(this);
        initLabels();
        edtPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        edtPhoneNumber.setOnDataChangedListener(data -> {
            if (!data.isEmpty()) {
                selectionCountry.setSelectionValue("");
                selectionCity.setSelectionValue("");
            }
        });
    }

    private void initLabels() {
        //Set text UpperCase programmatically because TextAllCaps = true not working
        txtFindNearByLabel.setText(getString(R.string.MH25_012).toUpperCase());
        txtFindByPhoneLabel.setText(getString(R.string.MH25_013).toUpperCase());
        txtFindByAddress.setText(getString(R.string.MH25_014).toUpperCase());

        txtFindNearByLabel.append(":");
        txtFindByPhoneLabel.append(":");
        txtFindByAddress.append(":");
    }

    @OnClick({R.id.txt_find_near_by, R.id.txt_find_friend, R.id.selection_country, R.id.selection_city,
            R.id.txt_phone_code_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_find_near_by:
                FindNearByActivity.startActivity(this);
                break;
            case R.id.txt_find_friend:
                checkDataValid();
                break;
            case R.id.selection_country:
                showSelectCountry();
                break;
            case R.id.selection_city:
                showSelectCity();
                break;
            case R.id.txt_phone_code_number:
                ChooseCountryActivity.startAcitivity(this, REQUEST_SELECT_PHONE_CODE);
                break;
        }
    }

    private void checkDataValid() {
        if (edtPhoneNumber.getText().isEmpty() &&
                selectionCountry.getSelectionValue().isEmpty()) {
            showNotifyDialog(getString(R.string.TB_1043));
        } else {
            if (edtPhoneNumber.getText().isEmpty()) {
                SearchFriendResultActivity.startActivity(this, selectionCountry.getSelectionValue(),
                        selectionCity.getSelectionValue());
            } else {
                SearchFriendResultActivity.startActivity(this,
                        txtPhoneCodeNumber.getText() + " " + edtPhoneNumber.getText());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHONE_CODE && resultCode == RESULT_OK) {
            PhoneCodeItem phoneCodeItem = data.getExtras().getParcelable(ChooseCountryActivity.SELECTED_COUNTRY);
            txtPhoneCodeNumber.setText(phoneCodeItem.getCode());
        }
    }

    private void showSelectCountry() {
        CountryBottomSheet bsCountry = new CountryBottomSheet();
        bsCountry.setSelectedCountry(selectionCountry.getSelectionValue());
        bsCountry.setOnCountryClickListener(countryName -> {
            selectionCountry.setSelectionValue(countryName);
            selectionCity.setSelectionValue("");
        });
                edtPhoneNumber.setText("");
        bsCountry.show(getSupportFragmentManager(), bsCountry.getTag());
    }

    private void showSelectCity() {
        List<ProvinceModel> provinceList = LocationConstantCN.getInstance()
                .getAllProvinceIn(selectionCountry.getSelectionValue());
        if (provinceList.size() > 0) {
            ProvinceBottomSheet bsProvince = new ProvinceBottomSheet();
            bsProvince.setProvinceList(provinceList);
            bsProvince.setSelectedProvince(selectionCity.getSelectionValue());
            bsProvince.setOnProvinceClickListener(provinceName ->
                    selectionCity.setSelectionValue(provinceName));
            bsProvince.show(getSupportFragmentManager(), bsProvince.getTag());
        }
    }

    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, SearchFriendActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }
}
