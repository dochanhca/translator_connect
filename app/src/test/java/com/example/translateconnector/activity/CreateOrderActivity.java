package com.example.translateconnector.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.imoktranslator.customview.DateTimeView;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.presenter.CreateOrderPresenter;
import com.imoktranslator.service.HandleCacheDataService;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DateTimeUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.LocationConstantCN;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateOrderActivity extends BaseActivity implements CreateOrderPresenter.CreateOrderView, HeaderView.BackButtonClickListener {

    private static final int REQUEST_CODE_SELECT_ON_MAP = 11;

    @BindView(R.id.header_user_info)
    HeaderView headerUserInfo;
    @BindView(R.id.field_order_name)
    TextFieldView fieldOrderName;
    @BindView(R.id.select_translation_lang)
    SelectionView selectTranslationLang;
    @BindView(R.id.select_translation_type)
    SelectionView selectTranslationType;
    @BindView(R.id.txt_place_label)
    OpenSansBoldTextView txtPlaceLabel;
    @BindView(R.id.select_place)
    SelectionView selectPlace;
    @BindView(R.id.layout_select_on_map)
    LinearLayout layoutSelectOnMap;
    @BindView(R.id.selection_country)
    SelectionView selectionCountry;
    @BindView(R.id.layout_select_country)
    LinearLayout layoutSelectCountry;
    @BindView(R.id.selection_city)
    SelectionView selectionCity;
    @BindView(R.id.layout_select_city)
    LinearLayout layoutSelectCity;
    @BindView(R.id.field_address)
    TextFieldView fieldAddress;
    @BindView(R.id.layout_select_address)
    LinearLayout layoutSelectAddress;
    @BindView(R.id.txt_from_label)
    OpenSansBoldTextView txtFromLabel;
    @BindView(R.id.date_time_from)
    DateTimeView dateTimeFrom;
    @BindView(R.id.txt_to_label)
    OpenSansBoldTextView txtToLabel;
    @BindView(R.id.date_time_to)
    DateTimeView dateTimeTo;
    @BindView(R.id.txt_experience_label)
    OpenSansBoldTextView txtExperienceLabel;
    @BindView(R.id.selection_experience)
    SelectionView selectionExperience;
    @BindView(R.id.txt_gender_label)
    OpenSansBoldTextView txtGenderLabel;
    @BindView(R.id.selection_gender)
    SelectionView selectionGender;
    @BindView(R.id.txt_expiration_label)
    OpenSansBoldTextView txtExpirationLabel;
    @BindView(R.id.date_time_expired)
    DateTimeView dateTimeExpired;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.btn_create_order)
    OpenSansBoldTextView btnCreateOrder;
    @BindView(R.id.bt_cancel)
    OpenSansBoldTextView btCancel;
    @BindView(R.id.txt_quality_label)
    TextView txtQualityLabel;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.txt_gender_error)
    TextView txtGenderError;

    private OrderModel orderModel;

    private String longitude;
    private String latitude;

    private List<String> translationLangs;
    private List<String> translationTypes;
    private List<String> experiences;
    private List<String> genders;

    private CreateOrderPresenter createOrderPresenter;
    private BroadcastReceiver shutdownReceiver = new ShutdownReceiver();
    private boolean isBackPressed;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    protected void initViews() {
        orderModel = LocalSharedPreferences.getInstance(this).getCachedOrder();
        if (orderModel == null) {
            orderModel = new OrderModel();
        }

        createOrderPresenter = new CreateOrderPresenter(this, this);

        startService();
        registerShutdownReceiver();
        initLabels();
        initDateTimePickerListener();
        initSelectionList();
        setSelectOnMapToDefault();
        headerUserInfo.setCallback(this);

        txtGenderError.setText(String.format(getString(R.string.TB_1019), getString(R.string.MH11_018)));
        fieldOrderName.setOnTextFieldErrorListener(s ->
                fieldOrderName.setError(s.toString().isEmpty() ?
                        String.format(getString(R.string.TB_1001), getString(R.string.MH11_002)) : ""));

        fillDataToViews();
        initDataChangeListener();
    }

    private void initDataChangeListener() {
        fieldOrderName.setOnDataChangedListener(data -> orderModel.setName(data));
        fieldAddress.setOnDataChangedListener(data -> orderModel.setAddress(data));
        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                orderModel.setDescription(s.toString());
            }
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> orderModel.setQuality(rating));
    }

    private void fillDataToViews() {
        fieldOrderName.setText(orderModel.getName() == null ? "" : orderModel.getName());

        if (orderModel.getTranslationLang() > 0) {
            selectTranslationLang.setSelectionValue(translationLangs.get(orderModel.getTranslationLang() - 1));
        }

        if (orderModel.getTranslationType() > 0) {
            selectTranslationType.setSelectionValue(translationTypes.get(orderModel.getTranslationType() - 1));
        }

        if (!TextUtils.isEmpty(orderModel.getExpirationDate())) {
            dateTimeExpired.setSelectedDate(initCalenderFromString(orderModel.getExpirationDate()));
        }

        if (!TextUtils.isEmpty(orderModel.getFromDate())) {
            dateTimeFrom.setSelectedDate(initCalenderFromString(orderModel.getFromDate()));
        }

        if (!TextUtils.isEmpty(orderModel.getToDate())) {
            dateTimeTo.setSelectedDate(initCalenderFromString(orderModel.getToDate()));
        }

        if (orderModel.getExperience() > 0) {
            selectionExperience.setSelectionValue(experiences.get(orderModel.getExperience() - 1));
        }

        if (orderModel.getGender() > 0) {
            selectionGender.setSelectionValue(genders.get(orderModel.getGender() - 1));
        }

        if (orderModel.getQuality() > 0) {
            ratingBar.setRating((float) orderModel.getQuality());
        }

        edtDescription.setText(orderModel.getDescription() == null ? "" : orderModel.getDescription());

        if (addressSettingByMap()) {
            setLocationByMapData(orderModel.getAddress(), orderModel.getLatitude(), orderModel.getLongitude());
        } else if (addressSettingByFilter()) {
            setLocationByFilter(orderModel.getCountry(), orderModel.getCity(), orderModel.getAddress());
        }
    }

    private Calendar initCalenderFromString(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeUtils.convertDateFromString(date, DateTimeUtils.YMD_HMS_FORMAT));
        return calendar;
    }

    private boolean addressSettingByMap() {
        return orderModel.getAddressType() == Constants.ADDRESS_TYPE_MAP;
    }

    private boolean addressSettingByFilter() {
        return orderModel.getAddressType() == Constants.ADDRESS_TYPE_FILTER;
    }

    private void setLocationByMapData(String mapAddress, String lat, String lon) {
        if (TextUtils.isEmpty(mapAddress)) {
            setSelectOnMapToDefault();
        } else {
            selectPlace.setSelectionValue(mapAddress);
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
            selectionCountry.setSelectionValue(country);
        }

        if (TextUtils.isEmpty(city)) {
            setCityToDefault();
        } else {
            selectionCity.setSelectionValue(city);
        }

        if (TextUtils.isEmpty(address)) {
            setAddressToDefault();
        } else {
            fieldAddress.setText(address);
        }

        setSelectOnMapToDefault();
    }

    private void registerShutdownReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, intentFilter);
    }

    private void initSelectionList() {
        translationLangs = convertArrToListString(R.array.arr_language);
        genders = convertArrToListString(R.array.arr_gender_order);
        translationTypes = convertArrToListString(R.array.arr_translation_type);
        experiences = convertArrToListString(R.array.arr_experience);
    }

    private void initDateTimePickerListener() {
        dateTimeFrom.setOnDateTimeSelectListener(new DateTimeView.OnDateTimeSelectListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                orderModel.setFromDate(DateTimeUtils.convertDateToString(dateTimeFrom.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));

            }

            @Override
            public void onTimeSelected(Date selectedTime) {
                orderModel.setFromDate(DateTimeUtils.convertDateToString(dateTimeFrom.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));
                dateTimeFrom.setError(!selectedTime.after(Calendar.getInstance().getTime())
                        ? getString(R.string.TB_1022) : "");
            }
        });

        dateTimeTo.setOnDateTimeSelectListener(new DateTimeView.OnDateTimeSelectListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                orderModel.setToDate(DateTimeUtils.convertDateToString(dateTimeTo.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));
            }

            @Override
            public void onTimeSelected(Date selectedTime) {
                orderModel.setToDate(DateTimeUtils.convertDateToString(dateTimeTo.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));
                dateTimeTo.setError(!isDateToValid() ? getString(R.string.TB_1024) : "");
            }
        });

        dateTimeExpired.setOnDateTimeSelectListener(new DateTimeView.OnDateTimeSelectListener() {
            @Override
            public void onDateSelected(Date selectedDate) {
                orderModel.setExpirationDate(DateTimeUtils.convertDateToString(dateTimeExpired.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));
            }

            @Override
            public void onTimeSelected(Date selectedTime) {
                orderModel.setExpirationDate(DateTimeUtils.convertDateToString(dateTimeExpired.getSelectedDate(),
                        DateTimeUtils.YMD_HMS_FORMAT));
                dateTimeExpired.setError(!selectedTime.after(Calendar.getInstance().getTime())
                        ? getString(R.string.TB_1023) : "");
            }
        });

    }

    private void initLabels() {
        //Set text UpperCase programmatically because TextAllCaps = true not working
        txtPlaceLabel.setText(getString(R.string.MH11_005).toUpperCase());
        txtFromLabel.setText(getString(R.string.MH11_030).toUpperCase());
        txtToLabel.setText(getString(R.string.MH11_015).toUpperCase());
        txtExperienceLabel.setText(getString(R.string.MH11_016).toUpperCase());
        txtExpirationLabel.setText(getString(R.string.MH11_021).toUpperCase());
        txtQualityLabel.setText(getString(R.string.MH11_024).toUpperCase());
        txtGenderLabel.setText(getString(R.string.MH11_018).toUpperCase());

        txtPlaceLabel.append(":");
        txtFromLabel.append(":");
        txtToLabel.append(":");
        txtExperienceLabel.append(":");
        txtGenderLabel.append(":");
        txtExpirationLabel.append(":");
        txtQualityLabel.append(":");

        initRequiredLabel(txtPlaceLabel);
        initRequiredLabel(txtFromLabel);
        initRequiredLabel(txtToLabel);
        initRequiredLabel(txtExpirationLabel);
    }

    private void initRequiredLabel(TextView textView) {
        Spannable asterisk = new SpannableString("*");
        asterisk.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.salmon_pink)), 0, asterisk.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.append(asterisk);
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
            Intent intent = builder.build(this);
            startActivityForResult(intent, REQUEST_CODE_SELECT_ON_MAP);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    private void setAddressToDefault() {
        fieldAddress.setText("");
    }

    private void setCityToDefault() {
        selectionCity.setSelectionValue("");
    }

    private void setCountryToDefault() {
        selectionCountry.setSelectionValue("");
    }

    private void setSelectOnMapToDefault() {
        selectPlace.setSelectionValue(R.string.MH11_006);
        latitude = null;
        longitude = null;
    }

    private void showSelectTranslationLangs() {
        CustomBottomSheetFragment bsTransLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_003));
        bsTransLanguage.setOptions(translationLangs);
        bsTransLanguage.setSelectedPosition(getSelectedPosition(translationLangs,
                selectTranslationLang.getSelectionValue()));
        bsTransLanguage.setListener(position -> {
            selectTranslationLang.setSelectionValue(translationLangs.get(position));
            selectTranslationLang.setError("");
            orderModel.setTranslationLang(position + 1);
        });
        bsTransLanguage.show(getSupportFragmentManager(), bsTransLanguage.getTag());
    }

    private void showSelectTranslationType() {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_004));
        bottomSheetFragment.setOptions(translationTypes);
        bottomSheetFragment.setSelectedPosition(getSelectedPosition(translationTypes,
                selectTranslationType.getSelectionValue()));
        bottomSheetFragment.setListener(position -> {
            selectTranslationType.setSelectionValue(translationTypes.get(position));
            selectTranslationType.setError("");
            orderModel.setTranslationType(position + 1);
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void showSelectExperience() {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_019));
        bottomSheetFragment.setOptions(experiences);
        bottomSheetFragment.setSelectedPosition(getSelectedPosition(experiences,
                selectionExperience.getSelectionValue()));
        bottomSheetFragment.setListener(position -> {
            selectionExperience.setSelectionValue(experiences.get(position));
            selectionExperience.setError("");
            orderModel.setExperience(position + 1);
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void showSelectGender() {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_018));
        bottomSheetFragment.setOptions(genders);
        bottomSheetFragment.setSelectedPosition(getSelectedPosition(genders,
                selectionGender.getSelectionValue()));
        bottomSheetFragment.setListener(position -> {
            selectionGender.setSelectionValue(genders.get(position));
            txtGenderError.setVisibility(View.GONE);
            orderModel.setGender( position + 1);
        });
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private int getSelectedPosition(List<String> selectionList, String value) {
        return TextUtils.isEmpty(value) ? -1 : selectionList.indexOf(value);
    }

    private void showSelectCountry() {
        CountryBottomSheet bsCountry = new CountryBottomSheet();
        bsCountry.setSelectedCountry(selectionCountry.getSelectionValue());
        bsCountry.setOnCountryClickListener(countryName -> {
            selectionCountry.setSelectionValue(countryName);
            setSelectOnMapToDefault();
            setCityToDefault();
            setAddressToDefault();
            fieldAddress.setError("");
            orderModel.setCountry(countryName);
            orderModel.setAddressType(getAddressType());

        });
        bsCountry.show(getSupportFragmentManager(), bsCountry.getTag());
    }

    private void showSelectCity() {
        List<ProvinceModel> provinceList = LocationConstantCN.getInstance()
                .getAllProvinceIn(selectionCountry.getSelectionValue());
        if (provinceList.size() > 0) {
            ProvinceBottomSheet bsProvince = new ProvinceBottomSheet();
            bsProvince.setProvinceList(provinceList);
            bsProvince.setSelectedProvince(selectionCity.getSelectionValue());
            bsProvince.setOnProvinceClickListener(provinceName -> {
                selectionCity.setSelectionValue(provinceName);
                setAddressToDefault();
                orderModel.setCity(provinceName);
            });
            bsProvince.show(getSupportFragmentManager(), bsProvince.getTag());
        }
    }

    private void createOrder() {
        if (checkDataValid()) {
            createOrderPresenter.createOrder(orderModel);
        } else {
            requestFocus();
        }
    }

    private boolean checkDataValid() {
        fieldOrderName.setError(fieldOrderName.getText().isEmpty() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH11_002)) : "");

        selectTranslationLang.setError(selectTranslationLang.getSelectionValue().isEmpty() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH11_003)) : "");

        selectTranslationType.setError(selectTranslationType.getSelectionValue().isEmpty() ?
                String.format(getString(R.string.TB_1019), getString(R.string.MH11_004)) : "");

        fieldAddress.setError(!isAddressValid() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH11_005)) : "");

        dateTimeFrom.setError(!dateTimeFrom.getSelectedDate().after(Calendar.getInstance().getTime())
                ? getString(R.string.TB_1022) : "");

        dateTimeExpired.setError(!dateTimeExpired.getSelectedDate().after(Calendar.getInstance().getTime())
                ? getString(R.string.TB_1023) : "");

        dateTimeTo.setError(!isDateToValid() ? getString(R.string.TB_1024) : "");


        return isAllFieldValid();
    }

    private boolean isAllFieldValid() {
        return !fieldOrderName.isError() && !selectTranslationLang.isError()
                && !selectTranslationType.isError() && !dateTimeFrom.isError()
                && !dateTimeTo.isError() && !dateTimeExpired.isError()
                && dateTimeFrom.getSelectedDate().after(Calendar.getInstance().getTime())
                && isDateToValid()
                && dateTimeExpired.getSelectedDate().after(Calendar.getInstance().getTime());
    }

    private boolean isAddressValid() {
        return (latitude != null && longitude != null) ||
                !selectionCountry.getSelectionValue().isEmpty();
    }

    private void requestFocus() {
        if (fieldOrderName.isError())
            showKeyboard(fieldOrderName);
    }

    private boolean isDateToValid() {
        // Trick to Make Start Date always larger than End Date 1 minutes if two date is same time
        Calendar compareDate = (Calendar) dateTimeFrom.getCalendar().clone();
        compareDate.add(Calendar.MINUTE, 1);

        return dateTimeTo.getSelectedDate().after(compareDate.getTime());
    }

    private int getAddressType() {
        if (TextUtils.isEmpty(selectPlace.getSelectionValue()) ||
                selectPlace.getSelectionValue().equals(getString(R.string.MH11_006))) {
            return Constants.ADDRESS_TYPE_FILTER;
        }
        return Constants.ADDRESS_TYPE_MAP;
    }


    @OnClick({R.id.layout_select_on_map, R.id.selection_country, R.id.selection_city,
            R.id.btn_create_order, R.id.bt_cancel, R.id.select_translation_lang,
            R.id.selection_gender, R.id.select_translation_type, R.id.selection_experience})
    public void onViewClicked(View view) {
        clearFocusOnEdittext();
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.layout_select_on_map:
                openMap();
                break;
            case R.id.selection_country:
                showSelectCountry();
                break;
            case R.id.selection_city:
                showSelectCity();
                break;
            case R.id.btn_create_order:
                createOrder();
                break;
            case R.id.select_translation_lang:
                showSelectTranslationLangs();
                break;
            case R.id.select_translation_type:
                showSelectTranslationType();
                break;
            case R.id.selection_gender:
                showSelectGender();
                break;
            case R.id.selection_experience:
                showSelectExperience();
                break;
            case R.id.bt_cancel:
                onBackPressed();
                break;
        }
    }

    private void clearFocusOnEdittext() {
        fieldOrderName.getEdtValue().clearFocus();
        fieldAddress.getEdtValue().clearFocus();
        edtDescription.clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ON_MAP && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            latitude = String.valueOf(place.getLatLng().latitude);
            longitude = String.valueOf(place.getLatLng().longitude);
            selectPlace.setSelectionValue(place.getAddress().toString());
            setCountryToDefault();
            setCityToDefault();
            setAddressToDefault();
            fieldAddress.setError("");
            orderModel.setAddress(place.getAddress().toString());
            orderModel.setAddressType(getAddressType());
            orderModel.setLatitude(latitude);
            orderModel.setLongitude(longitude);
        }
    }

    @Override
    public void onCreateOrderSuccess() {
        isBackPressed = true;
        Toast.makeText(this, getString(R.string.TB_1027), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (checkDataChanged()) {
            showNotifyDialog(getString(R.string.MH10_031), new NotifyDialog.OnNotifyCallback() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onOk(Object... obj) {
                    isBackPressed = true;
                    finish();
                }
            });
        } else {
            isBackPressed = true;
            finish();
        }
    }

    @Override
    public void onDestroy() {
        if (!isBackPressed) {
            //App being killed by recent button
            LocalSharedPreferences.getInstance(this).saveCacheOrder(orderModel);
        }
        HandleCacheDataService.stopService(this);
        unregisterReceiver(shutdownReceiver);
        super.onDestroy();
    }

    private void startService() {
        HandleCacheDataService.startService(this);
    }

    private boolean checkDataChanged() {
        return !TextUtils.isEmpty(fieldOrderName.getText()) ||
                !TextUtils.isEmpty(selectTranslationLang.getSelectionValue())
                || !TextUtils.isEmpty(selectTranslationType.getSelectionValue())
                || isAddressValid() || dateTimeFrom.getSelectedDate().after(Calendar.getInstance().getTime())
                || dateTimeExpired.getSelectedDate().after(Calendar.getInstance().getTime())
                || isDateToValid() || !TextUtils.isEmpty(selectionExperience.getSelectionValue())
                || !TextUtils.isEmpty(selectionGender.getSelectionValue())
                || ratingBar.getRating() != 0 || !TextUtils.isEmpty(edtDescription.getText());
    }

    public class ShutdownReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Insert code here
            Log.e(TAG, "Device Power off");
            LocalSharedPreferences.getInstance(CreateOrderActivity.this).saveCacheOrder(orderModel);
        }
    }
}
