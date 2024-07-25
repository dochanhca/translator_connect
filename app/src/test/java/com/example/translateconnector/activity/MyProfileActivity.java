package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.presenter.BaseProfileActivity;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FontUtils;
import com.imoktranslator.utils.GenderUtils;
import com.imoktranslator.utils.LocationConstantCN;
import com.imoktranslator.utils.PhoneCodeManager;
import com.imoktranslator.utils.Validator;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by tvoer on 3/29/18.
 */

public class MyProfileActivity extends BaseProfileActivity implements HeaderView.BackButtonClickListener {
    @BindView(R.id.header_user_info)
    HeaderView headerView;
    @BindView(R.id.field_name)
    TextFieldView fieldName;
    @BindView(R.id.field_phone)
    TextFieldView fieldPhone;
    @BindView(R.id.field_email)
    TextFieldView fieldEmail;
    @BindView(R.id.tv_country)
    OpenSansTextView tvCountry;
    @BindView(R.id.selection_gender)
    SelectionView selectionGender;
    @BindView(R.id.select_year_of_birth)
    SelectionView selectionDOB;
    @BindView(R.id.field_passport)
    TextFieldView fieldPassport;
    @BindView(R.id.register_translator)
    LinearLayout btRegisterTranslator;
    @BindView(R.id.txt_location_label)
    TextView txtLocationLabel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void initViews() {
        super.initViews();
        headerView.setCallback(this);
        fieldPhone.getEdtValue().setEnabled(false);
        fieldAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);

        fieldName.setOnTextFieldErrorListener(this::nameValidation);
        fieldEmail.setOnTextFieldErrorListener(this::emailValidation);
        fieldPassport.setInputType(InputType.TYPE_CLASS_NUMBER);
        selectOnMap.setSelectionValueFont(FontUtils.getInstance().getOpenSanBold());

        txtLocationLabel.setText(getString(R.string.MH11_005).toUpperCase());
        Spannable asterisk = new SpannableString("*");
        asterisk.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.salmon_pink)), 0, asterisk.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtLocationLabel.append(asterisk);
        populateDataToViews();
    }

    @Override
    public void updateSuccessful(PersonalInfo personalInfo, boolean isUpdateAvatarOnly) {
        Toast.makeText(this, getString(R.string.TB_1026), Toast.LENGTH_SHORT).show();
        this.personalInfo = personalInfo;
        populateDataToViews();
        photoFile =null;
        bytes = null;
    }

    @Override
    public List<Image> getImages() {
        return null;
    }

    @Override
    public boolean isNeedUploadMoreCertificates() {
        return false;
    }

    @Override
    protected void onDOBSelected(String year) {
        selectionDOB.setSelectionValue(year);
    }

    @Override
    protected void onCountrySelected(String countryName) {
        selectCountry.setSelectionValue(countryName);
        setSelectOnMapToDefault();
        setCityToDefault();
        setAddressToDefault();
    }

    @Override
    protected void onCitySelected(String provinceName) {
        selectCity.setSelectionValue(provinceName);
        setAddressToDefault();
    }


    @Override
    protected void onAvatarDeleted() {
        Glide.with(this).load(personalInfo.getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(profileImage);
    }

    @Override
    protected void onGenderSelected(int position) {
        selectionGender.setSelectionValue(genders.get(position));
    }

    @OnClick({R.id.layout_profile_image, R.id.selection_gender, R.id.select_year_of_birth,
            R.id.select_place, R.id.bt_save, R.id.bt_cancel, R.id.select_country,
            R.id.bt_change_password, R.id.start_qr_code, R.id.select_city,
            R.id.register_translator})
    public void onViewClicked(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.layout_profile_image:
                showAvatarOption();
                break;
            case R.id.selection_gender:
                showSelectGender(getSelectedPosition(genders, selectionGender.getSelectionValue()));
                break;

            case R.id.select_year_of_birth:
                int selectedYear = -1;
                try {
                    selectedYear = Integer.parseInt(selectionDOB.getSelectionValue());
                } catch (NumberFormatException ex) {

                }
                showSelectYearOfBirthOption(selectedYear);
                break;

            case R.id.select_place:
                openMap();
                break;

            case R.id.select_country:
                showSelectCountryOption(getSelectedCountry());
                break;

            case R.id.select_city:
                List<ProvinceModel> provinceList = LocationConstantCN.getInstance().getAllProvinceIn(getSelectedCountry());
                showSelectProvinceOption(provinceList, getSelectedProvince());
                break;

            case R.id.bt_cancel:
                onBackPressed();
                break;

            case R.id.bt_save:
                if (isDataValid()) {
                    updateUserInfo();
                }
                break;
            case R.id.bt_change_password:
                openChangePasswordScreen();
                break;

            case R.id.start_qr_code:
                QRCodeActivity.startActivity(this, true);
                break;

            case R.id.register_translator:
                if (personalInfo.getRole().equals(ROLE_TRANSLATOR)) {
                    Toast.makeText(this, getString(R.string.TB_1034), Toast.LENGTH_SHORT).show();
                } else {
                    registerTranslator();
                }
                break;
            default:
                break;
        }
    }

    private void registerTranslator() {
        Intent signUpTranslatorIntent = new Intent(this, SignUpTranslatorActivity.class);
        signUpTranslatorIntent.putExtra(SignUpTranslatorActivity.KEY_PERSONAL_INFO_OBJ, personalInfo);
        startActivityForResult(signUpTranslatorIntent, REQUEST_SIGN_UP_TRANSLATOR);
    }

    private void updateUserInfo() {
        if (personalInfo.getRole().equals(ROLE_TRANSLATOR)) {
            Toast.makeText(this, getString(R.string.TB_1044), Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkDataChanged()) {
            presenter.updateProfileInfo();
        } else if (isAddingAvatar()) {
            presenter.updateImageToServer(bytes, photoFile.getName(), true);
        }
    }

    private String getSelectedProvince() {
        return selectCity.getSelectionValue();
    }

    private boolean isDataValid() {
        String name = getName();
        String email = getEmail();
        nameValidation(name);
        emailValidation(email);

        fieldAddress.setError(!isAddressValid() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH11_005)) : "");

        return isAllErrorMessageGone();
    }

    private boolean isAddressValid() {
        return (latitude != null && longitude != null) ||
                !selectCountry.getSelectionValue().isEmpty();
    }

    private boolean isAllErrorMessageGone() {
        if (fieldName.isError()) {
            showKeyboard(fieldName.getEdtValue());
            return false;
        }

        if (fieldEmail.isError()) {
            showKeyboard(fieldEmail.getEdtValue());
            return false;
        }

        if (fieldAddress.isError()) {
            return false;
        }

        return true;
    }

    private void emailValidation(String email) {
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_008)));
        } else if (!Validator.validEmail(email)) {
            fieldEmail.setError(getString(R.string.TB_1005));
        } else {
            fieldEmail.setError("");
        }
    }

    private void nameValidation(String name) {
        if (TextUtils.isEmpty(name)) {
            fieldName.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)));
        } else {
            fieldName.setError("");
        }
    }

    private String getName() {
        return fieldName.getText();
    }

    private String getEmail() {
        return fieldEmail.getText();
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    private void populateDob() {
        String dob = personalInfo.getDob();
        selectionDOB.setSelectionValue(dob != null ? dob : "");
    }

    private void populateAddress() {
        if (!addressSettingByFilter()) {
            if (TextUtils.isEmpty(personalInfo.getAddress())) {
                setSelectOnMapToDefault();
            } else {
                selectOnMap.setSelectionValue(personalInfo.getAddress());
            }
            latitude = personalInfo.getLatitude();
            longitude = personalInfo.getLongitude();

            setCountryToDefault();
            setCityToDefault();
            setAddressToDefault();
        } else if (addressSettingByFilter()) {
            if (TextUtils.isEmpty(personalInfo.getCountry())) {
                setCountryToDefault();
            } else {
                selectCountry.setSelectionValue(personalInfo.getCountry());
            }

            if (TextUtils.isEmpty(personalInfo.getCity())) {
                setCityToDefault();
            } else {
                selectCity.setSelectionValue(personalInfo.getCity());
            }

            if (TextUtils.isEmpty(personalInfo.getAddress())) {
                setAddressToDefault();
            } else {
                fieldAddress.setText(personalInfo.getAddress());
            }

            setSelectOnMapToDefault();
        }
    }

    private String getSelectedCountry() {
        return selectCountry.getSelectionValue();
    }

    private void setSelectOnMapToDefault() {
        selectOnMap.setSelectionValue(R.string.MH11_006);
        latitude = null;
        longitude = null;
    }

    private boolean addressSettingByFilter() {
        return personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER;
    }

    private void populateYearOfBirth() {
        String year = personalInfo.getDob();
        selectionDOB.setSelectionValue(year != null ? year : "");
    }

    private void populateGender() {
        if (personalInfo.getGender() > 0 && personalInfo.getGender() <= genders.size()) {
            selectionGender.setSelectionValue(genders.get(personalInfo.getGender() - 1));
        } else {
            selectionGender.setSelectionValue("");
        }
    }

    private void populatePassport() {
        String passport = personalInfo.getIdentityCardNumber();
        fieldPassport.setText(passport != null ? passport : "");
    }

    private void populateName() {
        fieldName.setText(personalInfo.getName());
    }

    private void populatePhoneAndCountry() {
        fieldPhone.setText(personalInfo.getPhone());
        populateCountryFrom(personalInfo.getPhone());
    }

    private void populateEmail() {
        fieldEmail.setText(personalInfo.getEmail());
    }

    private void populateCountryFrom(String phone) {
        String countryName = PhoneCodeManager.getInstance().getCountryFrom(this, phone);
        tvCountry.setText(countryName);
    }

    /*handler all cases: url = null, invalid, valid url*/
    private void showAvatar() {
        Glide.with(this)
                .load(personalInfo.getAvatar())
                .error(R.drawable.img_default_avatar)
                .into(profileImage);
    }

    private void populateDataToViews() {
        photoFile = null;
        showAvatar();
        populateName();
        populatePhoneAndCountry();
        populateEmail();
        populatePassport();
        populateGender();
        populateYearOfBirth();
        populateAddress();
        populateDob();
    }

    @Override
    public PersonalInfoParam createUserInfoParam() {
        PersonalInfoParam param = new PersonalInfoParam();
        param.setName(getName());
        param.setPassport(getPassport());
        param.setGender(getGender());
        param.setDob(selectionDOB.getSelectionValue());
        param.setEmail(getEmail());

        param.setAddressType(getAddressType());
        param.setLat(getAddressType() == 0 ? latitude : null);
        param.setLon(getAddressType() == 0 ? longitude : null);
        param.setAddress(getAddressType() == 0 ? selectOnMap.getSelectionValue() : getAddress());
        param.setCountry(getSelectedCountry());
        param.setCity(getSelectedProvince());
        param.setTranslator(false);

        return param;
    }

    @Override
    public boolean isNeedUpdateProfileImage() {
        return photoFile != null;
    }

    private String getAddress() {
        return fieldAddress.getText();
    }

    private int getAddressType() {
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            return Constants.ADDRESS_TYPE_FILTER;
        }
        return Constants.ADDRESS_TYPE_MAP;
    }

    private int getGender() {
        return GenderUtils.convertGenderFrom(this, selectionGender.getSelectionValue());
    }

    private String getPassport() {
        return fieldPassport.getText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_UP_TRANSLATOR && resultCode == RESULT_OK) {
            if (data.getParcelableExtra(BaseProfileActivity.KEY_PERSONAL_INFO) != null) {
                personalInfo = data.getParcelableExtra(BaseProfileActivity.KEY_PERSONAL_INFO);
                populateDataToViews();
            }
        }
    }

    @Override
    protected boolean checkDataChanged() {

        if (!TextUtils.isEmpty(getName()) || !TextUtils.isEmpty(personalInfo.getName())) {
            if (!getName().equals(personalInfo.getName())) {
                return true;
            }
        }

        if (!TextUtils.isEmpty(getPassport()) || !TextUtils.isEmpty(personalInfo.getIdentityCardNumber())) {
            if (!getPassport().equals(personalInfo.getIdentityCardNumber())) {
                return true;
            }
        }

        if (getGender() != personalInfo.getGender()) {
            return true;
        }

        if (!TextUtils.isEmpty(selectionDOB.getSelectionValue()) || !TextUtils.isEmpty(personalInfo.getDob())) {
            if (!selectionDOB.getSelectionValue().equals(personalInfo.getDob())) {
                return true;
            }
        }

        if (!TextUtils.isEmpty(getEmail()) || !TextUtils.isEmpty(personalInfo.getEmail())) {
            if (!getEmail().equals(personalInfo.getEmail())) {
                return true;
            }
        }

        if (isAddressChanged()) {
            return true;
        }

        return false;
    }

    private boolean isAddressChanged() {
        int addressType = getAddressType();

        if (addressType == Constants.ADDRESS_TYPE_MAP) {

            if (!TextUtils.isEmpty(latitude) || !TextUtils.isEmpty(personalInfo.getLatitude())) {
                if (!latitude.equals(personalInfo.getLatitude())) {
                    return true;
                }
            }

            if (!TextUtils.isEmpty(longitude) || !TextUtils.isEmpty(personalInfo.getLongitude())) {
                if (!longitude.equals(personalInfo.getLongitude())) {
                    return true;
                }
            }
            return false;

        } else {
            if (!TextUtils.isEmpty(getSelectedCountry()) || !TextUtils.isEmpty(personalInfo.getCountry())) {
                if (!getSelectedCountry().equals(personalInfo.getCountry())) {
                    return true;
                }
            }

            if (!TextUtils.isEmpty(getSelectedProvince()) || !TextUtils.isEmpty(personalInfo.getCity())) {
                if (!getSelectedProvince().equals(personalInfo.getCity())) {
                    return true;
                }
            }

            if (!TextUtils.isEmpty(getAddress()) || !TextUtils.isEmpty(personalInfo.getAddress())) {
                if (!getAddress().equals(personalInfo.getAddress())) {
                    return true;
                }
            }
            return false;
        }

    }

}
