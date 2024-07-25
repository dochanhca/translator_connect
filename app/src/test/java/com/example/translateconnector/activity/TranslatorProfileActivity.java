package com.example.translateconnector.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.ListTagView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.presenter.BaseProfileActivity;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.FontUtils;
import com.imoktranslator.utils.GenderUtils;
import com.imoktranslator.utils.LocationConstantCN;
import com.imoktranslator.utils.PhoneCodeManager;
import com.imoktranslator.utils.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.imoktranslator.activity.ViewAttachFileActivity.KEY_ADDITIONAL_IMAGES;

public class TranslatorProfileActivity extends BaseProfileActivity
        implements HeaderView.BackButtonClickListener {
    private static final int REQUEST_ADD_MORE_CERTIFICATE = 111;
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
    @BindView(R.id.list_tag_view)
    ListTagView listTagTrans;
    @BindView(R.id.select_native_language)
    SelectionView selectNativeLanguage;
    @BindView(R.id.select_translation_language)
    SelectionView selectTranslationLanguage;
    @BindView(R.id.field_certificate)
    TextFieldView fieldCertificate;
    @BindView(R.id.list_certificate)
    ListTagView ltvCertificate;
    @BindView(R.id.selection_experience)
    SelectionView selectExperience;
    @BindView(R.id.select_graduation_year)
    SelectionView selectGraduationYear;
    @BindView(R.id.select_degree_classification)
    SelectionView selectGraduationType;
    @BindView(R.id.field_university)
    TextFieldView fieldUniversity;
    @BindView(R.id.tv_file_number)
    TextView tvFileNumber;
    @BindView(R.id.tv_file_attach_require)
    OpenSansTextView tvFileAttachRequire;
    @BindView(R.id.field_other_info)
    TextFieldView fieldOtherInfo;
    @BindView(R.id.bt_add_certificate)
    OpenSansBoldTextView btAddCertificate;
    @BindView(R.id.bt_save)
    TextView btnSave;

    protected List<String> listExperience;
    protected List<String> listGraduationType;
    protected List<String> listGraduationYear;
    private ArrayList<Image> additionalImageList;
    private boolean isWaitingConfirm;
    private int fileCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_translator_profile;
    }

    @Override
    protected void initViews() {
        super.initViews();

        headerView.setCallback(this);
        fieldPhone.getEdtValue().setEnabled(false);
        fieldPassport.getEdtValue().setEnabled(false);
        selectionGender.setEnabled(false);
        selectionDOB.setEnabled(false);
        fieldAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);

        fieldName.setOnTextFieldErrorListener(this::nameValidation);
        fieldEmail.setOnTextFieldErrorListener(this::emailValidation);
        fieldUniversity.setOnTextFieldErrorListener(this::universityValidation);
        fieldPassport.setInputType(InputType.TYPE_CLASS_NUMBER);
        selectOnMap.setSelectionValueFont(FontUtils.getInstance().getOpenSanBold());

        listGraduationType = Arrays.asList(getResources().getStringArray(R.array.graduation_type));
        listExperience = Arrays.asList(getResources().getStringArray(R.array.arr_experience));

        listGraduationYear = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1950; i--) {
            listGraduationYear.add(String.valueOf(i));
        }

        tvFileNumber.setTypeface(tvFileNumber.getTypeface(), Typeface.ITALIC);
        initCertificateChangeListener();

        setFileCount(personalInfo.getCertificates() != null ? personalInfo.getCertificates().size() : 0);
        populateDataToViews();
    }

    private void initCertificateChangeListener() {
        ltvCertificate.setOnListStateListener(new ListTagView.OnListStateListener() {
            @Override
            public void onAdded(String tagName) {
                if (ltvCertificate.getDataSet().size() == 9) {
                    btAddCertificate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRemoved(String tagName) {
                if (ltvCertificate.getDataSet().size() < 9) {
                    btAddCertificate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFullSlot() {

            }
        });
    }

    public void setFileCount(int count) {
        fileCount = count;
        tvFileNumber.setText(String.format(getString(R.string.MH09_021), String.valueOf(fileCount)));
        tvFileAttachRequire.setVisibility(count == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void emailValidation(String email) {
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_008)));
        } else if (!Validator.validEmail(email)) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(getString(R.string.TB_1005));
        } else {
            fieldEmail.setErrorVisible(false);
        }
    }

    private void nameValidation(String name) {
        if (TextUtils.isEmpty(name)) {
            fieldName.setErrorVisible(true);
            fieldName.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)));
        } else {
            fieldName.setErrorVisible(false);
        }
    }

    private void universityValidation(String university) {
        if (TextUtils.isEmpty(university)) {
            fieldUniversity.setErrorVisible(true);
            fieldUniversity.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH09_013)));
        } else {
            fieldUniversity.setErrorVisible(false);
        }
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
    protected void onAvatarDeleted() {
        Glide.with(this).load(personalInfo.getAvatar())
                .error(R.drawable.img_avatar_default)
                .into(profileImage);
    }

    @Override
    protected void onGenderSelected(int position) {
        selectionGender.setSelectionValue(genders.get(position));
    }

    @Override
    protected void onCitySelected(String provinceName) {
        selectCity.setSelectionValue(provinceName);
        setAddressToDefault();
    }

    @Override
    public void updateProfileInvalid() {
        super.updateProfileInvalid();
        isWaitingConfirm = true;
    }

    @Override
    protected boolean checkDataChanged() {
        if (isWaitingConfirm) {
            return false;
        }

        if (!TextUtils.isEmpty(getName()) || !TextUtils.isEmpty(personalInfo.getName())) {
            if (!getName().equals(personalInfo.getName())) {
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

        if (personalInfo.getForeignLanguages() != getIndexOfSelectedNativeLanguage() + 1) {
            return true;
        }

        if (personalInfo.getTranslateLanguages().size() != getTranIndexes().size()) {
            return true;
        } else {
            for (int i = 0; i < personalInfo.getTranslateLanguages().size(); i++) {
                if (!personalInfo.getTranslateLanguages().get(i).equals(getTranIndexes().get(i))) {
                    return true;
                }
            }
        }

        if (!personalInfo.getUniversity().equals(getUniversity())) {
            return true;
        }

        if (personalInfo.getDegreeClassification() != null || getGraduationType() != -1) {
            if (personalInfo.getDegreeClassification() != (getGraduationType() + 1)) {
                return true;
            }
        }

        if (!TextUtils.isEmpty(personalInfo.getYearOfGraduation()) || !TextUtils.isEmpty(getGraduationYear())) {
            if (!getGraduationYear().equals(personalInfo.getYearOfGraduation())) {
                return true;
            }
        }

        List<String> certificateList = new ArrayList<>(ltvCertificate.getDataSet());
        if (!TextUtils.isEmpty(getLastCertificateValue())) {
            certificateList.add(getLastCertificateValue());
        }
        if (personalInfo.getCertificateName() != null) {
            if (certificateList.size() != personalInfo.getCertificateName().size()) {
                return true;
            } else {
                for (int i = 0; i < certificateList.size(); i++) {
                    if (!certificateList.get(i).equals(personalInfo.getCertificateName().get(i))) {
                        return true;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(personalInfo.getOtherInfo()) || !TextUtils.isEmpty(getOtherInfo())) {
            if (!getOtherInfo().equals(personalInfo.getOtherInfo())) {
                return true;
            }
        }

        if (personalInfo.getYearExperience() != null || getExperience() != -1) {
            if ((getExperience() + 1) != personalInfo.getYearExperience()) {
                return true;
            }
        }

        if (getImages().size() > 0) {
            return true;
        }

        return false;
    }

    private boolean isAddressChanged() {
        int addressType = getAddressType();
        if (addressType != personalInfo.getAddressType()) {
            return true;
        } else {
            if (addressType == 0) {
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


    @OnClick({R.id.layout_profile_image, R.id.selection_gender, R.id.select_year_of_birth,
            R.id.select_place, R.id.bt_save, R.id.bt_cancel, R.id.select_country,
            R.id.bt_change_password, R.id.start_qr_code, R.id.select_city, R.id.select_native_language,
            R.id.select_translation_language, R.id.bt_add, R.id.select_degree_classification, R.id.select_graduation_year,
            R.id.bt_add_certificate, R.id.selection_experience, R.id.bt_upload_certificate})
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
                int selectedYear = TextUtils.isEmpty(selectionDOB.getSelectionValue()) ? -1 : Integer.parseInt(selectionDOB.getSelectionValue());
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
                    if (isWaitingConfirm) {
                        Toast.makeText(this, getString(R.string.TB_2099), Toast.LENGTH_LONG).show();
                    } else if (checkDataChanged()) {
                        presenter.updateProfileInfo();
                    } else if (isAddingAvatar()) {
                        presenter.updateImageToServer(bytes, photoFile.getName(), true);
                    }
                }
                break;
            case R.id.bt_change_password:
                openChangePasswordScreen();
                break;
            case R.id.start_qr_code:
                QRCodeActivity.startActivity(this, true);
                break;
            case R.id.select_native_language:
                showSelectNativeLanguage();
                break;

            case R.id.select_translation_language:
                showSelectTranslationLanguage();
                break;

            case R.id.bt_add:
                if (selectTranslationLanguage.hasValue()) {
                    listTagTrans.add(getCurrentSelectedTranslationLanguage());
                    selectTranslationLanguage.setSelectionValue("");
                }
                break;

            case R.id.select_degree_classification:
                showSelectGraduationType();
                break;
            case R.id.select_graduation_year:
                showSelectGraduationYear();
                break;

            case R.id.bt_add_certificate:
                if (!TextUtils.isEmpty(getLastCertificateValue())) {
                    ltvCertificate.add(getLastCertificateValue());
                    fieldCertificate.setText("");
                }
                break;

            case R.id.selection_experience:
                showSelectExperience();
                break;

            case R.id.bt_upload_certificate:
                Intent intent = new Intent(this, ViewAttachFileActivity.class);
                intent.putExtra(KEY_PERSONAL_INFO, personalInfo);
                startActivityForResult(intent, REQUEST_ADD_MORE_CERTIFICATE);
                break;
            default:
                break;
        }
    }

    private void showSelectExperience() {
        CustomBottomSheetFragment bsExperience = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_016));
        bsExperience.setOptions(listExperience);
        bsExperience.setSelectedPosition(listExperience.indexOf(selectExperience.getSelectionValue()));
        bsExperience.setListener(position -> selectExperience.setSelectionValue(listExperience.get(position)));
        bsExperience.show(getSupportFragmentManager(), bsExperience.getTag());
    }

    private String getLastCertificateValue() {
        return fieldCertificate.getText();
    }

    private void showSelectGraduationYear() {
        CustomBottomSheetFragment graduationYears = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_017));
        graduationYears.setOptions(listGraduationYear);
        graduationYears.setSelectedPosition(listGraduationYear.indexOf(selectGraduationYear.getSelectionValue()));
        graduationYears.setListener(position -> selectGraduationYear.setSelectionValue(listGraduationYear.get(position)));
        graduationYears.show(getSupportFragmentManager(), graduationYears.getTag());
    }

    private void showSelectGraduationType() {
        CustomBottomSheetFragment graduationTypes = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_016));
        graduationTypes.setOptions(listGraduationType);
        graduationTypes.setSelectedPosition(listGraduationType.indexOf(selectGraduationType.getSelectionValue()));
        graduationTypes.setListener(position -> selectGraduationType.setSelectionValue(listGraduationType.get(position)));
        graduationTypes.show(getSupportFragmentManager(), graduationTypes.getTag());
    }

    private void showSelectTranslationLanguage() {
        CustomBottomSheetFragment bsTransLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_015));
        bsTransLanguage.setOptions(listLanguage);
        bsTransLanguage.setSelectedPosition(getIndexOfCurrentSelectedTransLanguage());
        bsTransLanguage.setListener(position -> {
            String transLanguage = listLanguage.get(position);
            if (listTagTrans.isContain(transLanguage)) {
                showNotifyDialog("Ngôn ngữ phiên dịch đã bị trùng", "Đồng ý", new NotifyDialog.OnNotifyCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onOk(Object... obj) {
                        selectTranslationLanguage.setSelectionValue("");
                    }
                });
            } else {

            }
            selectTranslationLanguage.setSelectionValue(listLanguage.get(position));
        });
        bsTransLanguage.show(getSupportFragmentManager(), bsTransLanguage.getTag());
    }

    private int getIndexOfCurrentSelectedTransLanguage() {
        return listLanguage.indexOf(getCurrentSelectedTranslationLanguage());
    }

    private String getCurrentSelectedTranslationLanguage() {
        return selectTranslationLanguage.getSelectionValue();
    }

    private void showSelectNativeLanguage() {
        CustomBottomSheetFragment bsNativeLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_014));
        bsNativeLanguage.setOptions(listLanguage);
        bsNativeLanguage.setSelectedPosition(getIndexOfSelectedNativeLanguage());
        bsNativeLanguage.setListener(position -> selectNativeLanguage.setSelectionValue(listLanguage.get(position)));
        bsNativeLanguage.show(getSupportFragmentManager(), bsNativeLanguage.getTag());
    }

    private int getIndexOfSelectedNativeLanguage() {
        return listLanguage.indexOf(selectNativeLanguage.getSelectionValue());
    }

    private boolean isDataValid() {
        String name = getName();
        String email = getEmail();
        String university = getUniversity();
        nameValidation(name);
        emailValidation(email);
        universityValidation(university);

        return isAllErrorMessageGone();
    }

    private String getUniversity() {
        return fieldUniversity.getText();
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

        if (fieldUniversity.isError()) {
            showKeyboard(fieldUniversity.getEdtValue());
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MORE_CERTIFICATE && resultCode == RESULT_OK) {
            additionalImageList = data.getParcelableArrayListExtra(KEY_ADDITIONAL_IMAGES);
            fileCount = additionalImageList.size();
            setFileCount(fileCount);
        }
    }

    private void setSelectOnMapToDefault() {
        selectOnMap.setSelectionValue(R.string.MH11_006);
        latitude = null;
        longitude = null;
    }

    private String getSelectedCountry() {
        return selectCountry.getSelectionValue();
    }

    private String getSelectedProvince() {
        return selectCity.getSelectionValue();
    }

    @Override
    public PersonalInfoParam createUserInfoParam() {
        PersonalInfoParam param = new PersonalInfoParam();
        param.setName(getName());
        param.setPassport(getPassport());
        param.setGender(getGender());
        param.setDob(String.valueOf(getDob()));
        param.setEmail(getEmail());

        param.setAddressType(getAddressType());
        param.setLat(getAddressType() == 0 ? latitude : null);
        param.setLon(getAddressType() == 0 ? longitude : null);
        param.setAddress(getAddressType() == 0 ? selectOnMap.getSelectionValue() : getAddress());
        param.setCountry(getSelectedCountry());
        param.setCity(getSelectedProvince());

        param.setNativeLanguage(getIndexOfSelectedNativeLanguage() + 1);
        List<String> tranIndexes = getTranIndexes();
        param.setTransLanguage(tranIndexes);

        param.setUniversity(getUniversity());
        param.setDegreeClassification(getGraduationType() + 1);
        param.setGraduationYear(getGraduationYear());
        param.setExperience(getExperience() + 1);
        param.setOtherInfo(getOtherInfo());

        List<String> certificateList = new ArrayList<>(ltvCertificate.getDataSet());
        if (!TextUtils.isEmpty(getLastCertificateValue())) {
            certificateList.add(getLastCertificateValue());
        }
        param.setCertificates(certificateList);
        param.setTranslator(true);

        return param;
    }

    private List<String> getTranIndexes() {
        List<String> tranIndexes = new ArrayList<>();
        int tranLanguageIndex;
        for (String language : getTranslationLanguages()) {
            tranLanguageIndex = Integer.parseInt(language) + 1;
            tranIndexes.add(String.valueOf(tranLanguageIndex));
        }
        return tranIndexes;
    }

    private String getOtherInfo() {
        return fieldOtherInfo.getText();
    }

    private int getExperience() {
        return listExperience.indexOf(selectExperience.getSelectionValue());
    }

    private String getGraduationYear() {
        return selectGraduationYear.getSelectionValue();
    }

    private int getGraduationType() {
        return listGraduationType.indexOf(selectGraduationType.getSelectionValue());
    }

    public List<String> getTranslationLanguages() {
        List<String> tranLanguages = convertFrom(listTagTrans.getDataSet());
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

    private String getAddress() {
        return fieldAddress.getText();
    }

    private int getAddressType() {
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            return 1; //select by filter
        }
        return 0; // select by map
    }

    private int getDob() {
        try {
            String dobStr = selectionDOB.getSelectionValue();
            return Integer.parseInt(dobStr);
        } catch (NumberFormatException e) {
            return 1950;
        }
    }

    private int getGender() {
        return GenderUtils.convertGenderFrom(this, selectionGender.getSelectionValue());
    }

    private String getPassport() {
        return fieldPassport.getText();
    }

    private String getName() {
        return fieldName.getText();
    }

    private String getEmail() {
        return fieldEmail.getText();
    }

    @Override
    public boolean isNeedUpdateProfileImage() {
        return photoFile != null;
    }

    public void populateDataToViews() {
        photoFile = null;
        populateHeader();
        populateName();
        populatePhoneAndCountry();
        populateEmail();
        populatePassport();
        populateGender();
        populateYearOfBirth();
        populateAddress();
        populateDob();

        populateLanguages();
        populateCertificateData();
    }

    private void populateCertificateData() {

        fieldUniversity.setText(personalInfo.getUniversity());
        if (personalInfo.getDegreeClassification() > 0) {
            selectGraduationType.setSelectionValue(listGraduationType.get(personalInfo.getDegreeClassification() - 1));
        }

        selectGraduationYear.setSelectionValue(personalInfo.getYearOfGraduation());

        if (personalInfo.getYearExperience() > 0) {
            selectExperience.setSelectionValue(listExperience.get(personalInfo.getYearExperience() - 1));
        }

        List<String> certificateList = personalInfo.getCertificateName();
        if (certificateList != null && certificateList.size() > 0) {
            ltvCertificate.getDataSet().clear();
            int size = certificateList.size();
            if (size == 1) {
                fieldCertificate.setText(certificateList.get(0));
            } else {
                fieldCertificate.setText(certificateList.get(size - 1));
                for (int i = 0; i <= size - 2; i++) {
                    ltvCertificate.add(certificateList.get(i));
                }
            }
        }

        fieldOtherInfo.setText(personalInfo.getOtherInfo());
        btAddCertificate.setVisibility(ltvCertificate.getDataSet().size() == 9 ? View.GONE : View.VISIBLE);
    }

    private void populateLanguages() {
        if (personalInfo.getForeignLanguages() > 0) {
            selectNativeLanguage.setSelectionValue(listLanguage.get(personalInfo.getForeignLanguages() - 1));
        }
        listTagTrans.getDataSet().clear();
        for (String index : personalInfo.getTranslateLanguages()) {
            if (personalInfo.getTranslateLanguages().indexOf(index) != personalInfo.getTranslateLanguages().size() - 1) {
                listTagTrans.add(listLanguage.get(Integer.parseInt(index) - 1));
            } else {
                selectTranslationLanguage.setSelectionValue(listLanguage.get(Integer.parseInt(index) - 1));
            }
        }
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

    private void populateHeader() {
        showAvatar();
        btRegisterTranslator.setVisibility(View.GONE);
    }

    private void showAvatar() {
        Glide.with(this)
                .load(personalInfo.getAvatar())
                .error(R.drawable.img_default_avatar)
                .placeholder(R.drawable.img_avatar_default)
                .transform(new CircleTransform(this))
                .into(profileImage);
    }

    @Override
    public void updateSuccessful(PersonalInfo personalInfo, boolean isUpdateProfileOnly) {
        if (!isUpdateProfileOnly) {
            Toast.makeText(this, getString(R.string.TB_1035), Toast.LENGTH_LONG).show();
            isWaitingConfirm = true;
        } else {
            Toast.makeText(this, getString(R.string.TB_1052), Toast.LENGTH_LONG).show();
        }
        photoFile = null;
        bytes = null;
    }

    @Override
    public List<Image> getImages() {
        List<Image> images = new ArrayList<>();

        if (additionalImageList == null) {
            return images;
        }

        for (Image image : additionalImageList) {
            if (image.id != -1) {
                images.add(image);
            }
        }
        return images;
    }

    @Override
    public boolean isNeedUploadMoreCertificates() {
        return (getImages() != null && getImages().size() > 0);
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }
}
