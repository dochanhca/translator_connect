package com.example.translateconnector.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.imoktranslator.R;
import com.imoktranslator.bottomsheet.CountryBottomSheet;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.bottomsheet.ProvinceBottomSheet;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.ListTagView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.presenter.SignUpTranslatorStep2Presenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocationConstantCN;
import com.imoktranslator.utils.SerialUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ton on 4/6/18.
 */

public class TranslatorAddressAndLanguageFragment extends BaseFragment implements
        HeaderView.BackButtonClickListener, SignUpTranslatorStep2Presenter.SignUpTranslatorStep2View {
    private static final int REQUEST_CODE_SELECT_ON_MAP = 69;
    @BindView(R.id.header_address_and_language)
    HeaderView headerView;
    @BindView(R.id.select_place)
    SelectionView selectOnMap;
    @BindView(R.id.select_country)
    SelectionView selectCountry;
    @BindView(R.id.select_city)
    SelectionView selectCity;
    @BindView(R.id.field_address)
    TextFieldView fieldAddress;
    @BindView(R.id.list_tag_view)
    ListTagView listTagView;
    @BindView(R.id.select_native_language)
    SelectionView selectNativeLanguage;
    @BindView(R.id.select_translation_language)
    SelectionView selectTranslationLanguage;
    @BindView(R.id.bt_cancel)
    OpenSansBoldTextView btCancel;
    @BindView(R.id.txt_location_label)
    TextView txtLocationLabel;

    private SignUpTranslatorStep2Presenter presenter;
    private PersonalInfo personalInfo;
    private PersonalInfo backupPersonalInfo;
    private String latitude;
    private String longitude;
    private List<String> listLanguage;
    private boolean isHandleBackEvent = true;

    public static TranslatorAddressAndLanguageFragment newInstance(PersonalInfo personalInfo) {

        Bundle args = new Bundle();

        TranslatorAddressAndLanguageFragment fragment = new TranslatorAddressAndLanguageFragment();
        fragment.setArguments(args);
        fragment.personalInfo = personalInfo;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_translator_address_and_language;
    }

    @Override
    protected void initViews() {

        backupPersonalInfo = (PersonalInfo) SerialUtils.cloneObject(personalInfo);

        presenter = new SignUpTranslatorStep2Presenter(getContext(), this);
        headerView.setTittle(getString(R.string.MH09_026));
        headerView.setCallback(this);

        String[] arrLanguage = getResources().getStringArray(R.array.arr_language);
        listLanguage = Arrays.asList(arrLanguage);

        txtLocationLabel.setText(getString(R.string.MH11_005).toUpperCase());
        initRequiredLabel(txtLocationLabel);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (personalInfo != null) {
            populateDataFromPersonalInfo();
        }
        initDataChangedListener();
    }

    private void initDataChangedListener() {
        fieldAddress.setOnDataChangedListener(data -> personalInfo.setAddress(data));
    }

    @OnClick({R.id.select_place, R.id.select_country, R.id.select_city,
            R.id.select_native_language, R.id.select_translation_language,
            R.id.bt_add, R.id.bt_save, R.id.bt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_place:
                openMap();
                break;

            case R.id.select_country:
                showSelectCountryOption();
                break;

            case R.id.select_city:
                showSelectProvinceOption();
                break;

            case R.id.select_native_language:
                showSelectNativeLanguage();
                break;

            case R.id.select_translation_language:
                showSelectTranslationLanguage();
                break;

            case R.id.bt_add:
                if (selectTranslationLanguage.hasValue()) {
                    listTagView.add(getCurrentSelectedTranslationLanguage());
                    selectTranslationLanguage.setSelectionValue("");
                }
                break;

            case R.id.bt_save:
                presenter.onNextStepClicked();
                break;

            case R.id.bt_cancel:
                presenter.onCancelClicked(backupPersonalInfo);
                break;
            default:
                break;
        }
    }

    private void showSelectTranslationLanguage() {
        CustomBottomSheetFragment bsTransLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_015));
        bsTransLanguage.setOptions(listLanguage);
        bsTransLanguage.setSelectedPosition(getIndexOfCurrentSelectedTransLanguage());
        bsTransLanguage.setListener(position -> {
            String transLanguage = listLanguage.get(position);
            if (listTagView.isContain(transLanguage)) {
                showNotifyDialog(getString(R.string.TB_1086), new NotifyDialog.OnNotifyCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk(Object... obj) {
                        selectTranslationLanguage.setSelectionValue("");
                    }
                });
            } else {
                selectTranslationLanguage.setSelectionValue(listLanguage.get(position));
                addNewTransLang(position + 1);
            }
            selectTranslationLanguage.setError("");
        });
        bsTransLanguage.show(getChildFragmentManager(), bsTransLanguage.getTag());
    }

    private void addNewTransLang(int langIndex) {
        if (personalInfo.getTranslateLanguages() == null) {
            personalInfo.setTranslateLanguages(new ArrayList<>());
        }

        if (!personalInfo.getTranslateLanguages().contains(String.valueOf(langIndex))) {
            personalInfo.getTranslateLanguages().add(String.valueOf(langIndex));
        }
    }

    private void showSelectNativeLanguage() {
        CustomBottomSheetFragment bsNativeLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_014));
        bsNativeLanguage.setOptions(listLanguage);
        bsNativeLanguage.setSelectedPosition(listLanguage.indexOf(selectNativeLanguage.getSelectionValue()));
        bsNativeLanguage.setListener(position -> {
            selectNativeLanguage.setSelectionValue(listLanguage.get(position));
            personalInfo.setForeignLanguages(getIndexOfSelectedNativeLanguage());
            selectNativeLanguage.setError("");
        });
        bsNativeLanguage.show(getChildFragmentManager(), bsNativeLanguage.getTag());
    }

    private int getIndexOfCurrentSelectedTransLanguage() {
        return listLanguage.indexOf(getCurrentSelectedTranslationLanguage());
    }

    private void showSelectProvinceOption() {
        List<ProvinceModel> provinceList = LocationConstantCN.getInstance().getAllProvinceIn(getSelectedCountry());
        if (provinceList.size() > 0) {
            ProvinceBottomSheet bsProvince = new ProvinceBottomSheet();
            bsProvince.setProvinceList(provinceList);
            bsProvince.setSelectedProvince(getSelectedCity());
            bsProvince.setOnProvinceClickListener(provinceName -> {
                selectCity.setSelectionValue(provinceName);
                personalInfo.setCity(provinceName);
                setAddressToDefault();
            });
            bsProvince.show(getChildFragmentManager(), bsProvince.getTag());
        }
    }

    private void showSelectCountryOption() {
        CountryBottomSheet bsCountry = new CountryBottomSheet();
        bsCountry.setSelectedCountry(getSelectedCountry());
        bsCountry.setOnCountryClickListener(countryName -> {
            selectCountry.setSelectionValue(countryName);
            personalInfo.setCountry(countryName);
            personalInfo.setAddressType(Constants.ADDRESS_TYPE_FILTER);
            setSelectOnMapToDefault();
            setCityToDefault();
            setAddressToDefault();
            fieldAddress.setError("");
        });
        bsCountry.show(getChildFragmentManager(), bsCountry.getTag());
    }

    private void openMap() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            LatLng bottomLeft = new LatLng(Double.parseDouble(latitude) - 0.005, Double.parseDouble(longitude) - 0.005);
            LatLng topRight = new LatLng(Double.parseDouble(latitude) + 0.005, Double.parseDouble(longitude) + 0.005);
            LatLngBounds bounds = new LatLngBounds(bottomLeft, topRight);
            builder.setLatLngBounds(bounds);
        }
        try {
            Intent intent = builder.build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_SELECT_ON_MAP);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    private void setLocationByMapData(String mapAddress, String lat, String lon) {
        if (TextUtils.isEmpty(mapAddress)) {
            setSelectOnMapToDefault();
        } else {
            selectOnMap.setSelectionValue(mapAddress);
        }
        latitude = lat;
        longitude = lon;

        setCountryToDefault();
        setCityToDefault();
        setAddressToDefault();
    }

    private void setLocationByFilter(String country, String city, String address) {
        if (TextUtils.isEmpty(country)) {
            setCountryToDefault();
        } else {
            selectCountry.setSelectionValue(country);
        }

        if (TextUtils.isEmpty(city)) {
            setCityToDefault();
        } else {
            selectCity.setSelectionValue(city);
        }

        if (TextUtils.isEmpty(address)) {
            setAddressToDefault();
        } else {
            fieldAddress.setText(address);
        }

        setSelectOnMapToDefault();
    }

    private void populateDataFromPersonalInfo() {
        if (addressSettingByMap()) {
            setLocationByMapData(personalInfo.getAddress(), personalInfo.getLatitude(), personalInfo.getLongitude());
        } else if (addressSettingByFilter()) {
            setLocationByFilter(personalInfo.getCountry(), personalInfo.getCity(), personalInfo.getAddress());
        }

        if (personalInfo.getForeignLanguages() > 0) {
            selectNativeLanguage.setSelectionValue(listLanguage.get(personalInfo.getForeignLanguages() - 1));
        }

        if (personalInfo.getTranslateLanguages() == null)
            return;

        for (String language : personalInfo.getTranslateLanguages()) {
            if (personalInfo.getTranslateLanguages().indexOf(language) !=
                    personalInfo.getTranslateLanguages().size() - 1) {
                listTagView.add(listLanguage.get(Integer.parseInt(language)));
            } else {
                selectTranslationLanguage.setSelectionValue(listLanguage.get(Integer.parseInt(language) - 1));
            }
        }
    }

    private String getCurrentSelectedTranslationLanguage() {
        return selectTranslationLanguage.getSelectionValue();
    }

    private boolean addressSettingByFilter() {
        return personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER;
    }

    private void setAddressToDefault() {
        fieldAddress.setText("");
    }

    private void setCityToDefault() {
        selectCity.setSelectionValue("");
    }

    private void setCountryToDefault() {
        selectCountry.setSelectionValue("");
    }

    private void setSelectOnMapToDefault() {
        selectOnMap.setSelectionValue(R.string.MH11_006);
        latitude = null;
        longitude = null;
    }

    private boolean addressSettingByMap() {
        return personalInfo.getAddressType() == Constants.ADDRESS_TYPE_MAP;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ON_MAP && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getContext(), data);
            latitude = String.valueOf(place.getLatLng().latitude);
            longitude = String.valueOf(place.getLatLng().longitude);
            selectOnMap.setSelectionValue(place.getAddress().toString());
            personalInfo.setLatitude(latitude);
            personalInfo.setLongitude(longitude);
            personalInfo.setAddress(place.getAddress().toString());
            personalInfo.setAddressType(Constants.ADDRESS_TYPE_MAP);
            setCountryToDefault();
            setCityToDefault();
            setAddressToDefault();
        }
    }


    //implement methods of BaseImageUploadView

    @Override
    public String getPlaceSelectedByPlacePicker() {
        return selectOnMap.getSelectionValue();
    }

    @Override
    public String getSelectOnMapPlaceHolderValue() {
        return getString(R.string.MH11_006);
    }

    @Override
    public String getSelectedCountry() {
        return selectCountry.getSelectionValue();
    }

    @Override
    public int getIndexOfSelectedNativeLanguage() {
        return listLanguage.indexOf(selectNativeLanguage.getSelectionValue()) + 1;
    }

    @Override
    public void showLocationIsRequireWarning() {
        fieldAddress.setError(
                String.format(getString(R.string.TB_1001), getString(R.string.MH11_005)));
    }

    @Override
    public void hideLocationIsRequireWarning() {
        fieldAddress.setError("");
    }

    @Override
    public void showNativeLanguageIsRequireWarning() {
        selectNativeLanguage.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH09_014)));
    }

    @Override
    public void hideNativeLanguageIsRequireWarning() {
        selectNativeLanguage.setError("");
    }

    @Override
    public List<String> getTranslationLanguages() {
        List<String> tranLanguages = convertFrom(listTagView.getDataSet());
        String currentTranslationLanguageSelected = getCurrentSelectedTranslationLanguage();
        if (!TextUtils.isEmpty(currentTranslationLanguageSelected)) {
            tranLanguages.add(String.valueOf(listLanguage.indexOf(currentTranslationLanguageSelected)));
        }
        return tranLanguages;
    }

    private List<String> convertFrom(List<String> dataSet) {
        List<String> result = new ArrayList<>();
        for (String item : dataSet) {
            result.add(String.valueOf(listLanguage.indexOf(item)));
        }
        return result;
    }

    private void initRequiredLabel(TextView textView) {
        Spannable asterisk = new SpannableString("*");
        asterisk.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.salmon_pink)), 0, asterisk.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.append(asterisk);
    }

    @Override
    public void showTranslationLanguageIsRequireWarning() {
        selectTranslationLanguage.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH09_015)));
    }

    @Override
    public void hideTranslationLanguageIsRequireWarning() {
        selectTranslationLanguage.setError("");
    }

    @Override
    public String getAddress() {
        return fieldAddress.getText();
    }

    @Override
    public void goToNextStep() {
        switchFragment(CertificateInfoFragment.newInstance(personalInfo),
                new FragmentController.Option.Builder()
                        .useAnimation(true)
                        .addToBackStack(true)
                        .setType(FragmentController.Option.TYPE.ADD)
                        .build());
    }

    @Override
    public String getLatitude() {
        return latitude;
    }

    @Override
    public String getLongitude() {
        return longitude;
    }

    @Override
    public String getSelectedCity() {
        return selectCity.getSelectionValue();
    }

    @Override
    public void showNoticeDataChanged() {
        isHandleBackEvent = false;
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void backToPreScreen() {
        isHandleBackEvent = false;
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        if (isHandleBackEvent) {
            onViewClicked(btCancel);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void backButtonClicked() {
        onViewClicked(btCancel);
    }
}
