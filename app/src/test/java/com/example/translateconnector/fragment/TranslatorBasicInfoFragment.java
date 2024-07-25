package com.example.translateconnector.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.activity.DisplayImageProfileActivity;
import com.imoktranslator.bottomsheet.AvatarOptionBottomSheet;
import com.imoktranslator.bottomsheet.GenderBottomSheet;
import com.imoktranslator.bottomsheet.YearOfBirthBottomSheet;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.presenter.BasicInfoPresenter;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.GenderUtils;
import com.imoktranslator.utils.ImagesManager;
import com.imoktranslator.utils.PhoneCodeManager;
import com.imoktranslator.utils.SerialUtils;
import com.imoktranslator.utils.Validator;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tvoer on 4/5/18.
 */

public class TranslatorBasicInfoFragment extends BaseFragment implements
        HeaderView.BackButtonClickListener, BasicInfoPresenter.BasicInfoView {

    private static final String TAG = TranslatorBasicInfoFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    @BindView(R.id.header_register_translator)
    HeaderView headerView;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.field_name)
    TextFieldView fieldName;
    @BindView(R.id.field_phone)
    TextFieldView fieldPhone;
    @BindView(R.id.field_email)
    TextFieldView fieldEmail;
    @BindView(R.id.tv_country)
    OpenSansTextView tvCountry;
    @BindView(R.id.field_passport)
    TextFieldView fieldPassport;
    @BindView(R.id.select_gender)
    SelectionView selectGender;
    @BindView(R.id.select_year_of_birth)
    SelectionView selectDob;
    @BindView(R.id.bt_cancel)
    OpenSansBoldTextView btCancel;
    @BindView(R.id.tv_gender_n_dob_require)
    OpenSansTextView tvGenderAndDobRequire;


    private BasicInfoPresenter presenter;
    private PersonalInfo personalInfo;
    private PersonalInfo oldPersonalInfo;
    private File photoFile;
    private byte[] bytes;
    private boolean allowBack;

    private String[] genders;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_translator_basic_info;
    }

    public static TranslatorBasicInfoFragment newInstance(PersonalInfo personalInfo) {
        TranslatorBasicInfoFragment fragment = new TranslatorBasicInfoFragment();
        fragment.personalInfo = personalInfo;
        return fragment;
    }

    @Override
    protected void initViews() {
        oldPersonalInfo = (PersonalInfo) SerialUtils.cloneObject(personalInfo);

        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH09_026));
        fieldPhone.getEdtValue().setEnabled(false);
        fieldPassport.setInputType(InputType.TYPE_CLASS_NUMBER);

        genders = getResources().getStringArray(R.array.arr_gender);

        initErrorListener();
        initDataChangeListener();
    }

    private void initErrorListener() {
        fieldName.setOnTextFieldErrorListener(s ->
                fieldName.setError(TextUtils.isEmpty(s) ?
                        String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)) : ""));
        fieldEmail.setOnTextFieldErrorListener(s -> {
            if (TextUtils.isEmpty(s)) {
                fieldEmail.setErrorVisible(true);
                fieldEmail.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_008)));
            } else if (!Validator.validEmail(s)) {
                fieldEmail.setErrorVisible(true);
                fieldEmail.setError(getString(R.string.TB_1005));
            } else {
                fieldEmail.setErrorVisible(false);
            }
        });

        fieldPassport.setOnTextFieldErrorListener(s ->
                fieldPassport.setError(TextUtils.isEmpty(s) ?
                        String.format(getString(R.string.TB_1001), getString(R.string.MH04_008)) : ""));
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        if (allowBack) {
            return false;
        } else {
            onViewClicked(btCancel);
            return true;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new BasicInfoPresenter(getContext(), this);

        populateDataFromPersonalInfo();
    }

    private void initDataChangeListener() {
        fieldName.setOnDataChangedListener(data -> personalInfo.setName(data));
        fieldPassport.setOnDataChangedListener(data -> personalInfo.setIdentityCardNumber(data));
        fieldEmail.setOnDataChangedListener(data -> personalInfo.setEmail(data));
    }

    private void populateCountryFrom(String phone) {
        String countryName = PhoneCodeManager.getInstance().getCountryFrom(getContext(), phone);
        tvCountry.setText(countryName);
    }

    private void populatePhone(String phone) {
        fieldPhone.setText(phone);
    }

    private void populateDataFromPersonalInfo() {
        showAvatar(personalInfo.getAvatar());
        populateName(personalInfo.getName());
        populatePhone(personalInfo.getPhone());
        populateCountryFrom(personalInfo.getPhone());
        populatePassport(personalInfo.getIdentityCardNumber());
        populateGender(personalInfo.getGender());
        populateYearOfBirth(personalInfo.getDob());
        populateEmail(personalInfo.getEmail());

    }

    private String getAvatar() {
        String avatar = photoFile == null ? personalInfo.getAvatar() : photoFile.getAbsolutePath();
        if (avatar == null) {
            avatar = "";
        }
        return avatar;
    }

    private String getEmail() {
        return fieldEmail.getText();
    }

    private String getDob() {
        return selectDob.getSelectionValue();
    }

    private int getGender() {
        return GenderUtils.convertGenderFrom(getContext(), selectGender.getSelectionValue());
    }

    private String getName() {
        return fieldName.getText();
    }

    private String getPhone() {
        return fieldPhone.getText();
    }

    private String getPassport() {
        return fieldPassport.getText();
    }

    private void populateYearOfBirth(String year) {
        if (year != null) {
            selectDob.setSelectionValue(year);
        } else {
            selectDob.setHint("");
        }
    }

    private void populateGender(int gender) {
        if (gender > 0) {
            selectGender.setSelectionValue(genders[gender - 1]);
        }
    }

    private void populatePassport(String passport) {
        fieldPassport.setText(passport != null ? passport : "");
    }

    private void populateName(String name) {
        fieldName.setText(name);
    }

    private void populateEmail(String email) {
        fieldEmail.setText(email);
    }

    private void showAvatar(String path) {
        Glide.with(this).load(path).error(R.drawable.img_default_avatar).into(profileImage);
    }

    public void showAvatarOption() {
        AvatarOptionBottomSheet bottomSheetFragment = new AvatarOptionBottomSheet();
        bottomSheetFragment.setOnOptionClickListener(view -> {
            switch (view.getId()) {
                case R.id.tv_display_avatar:
                    displayAvatar();
                    break;
                case R.id.tv_capture_avatar:
                    presenter.requestCameraPermission(getActivity());
                    break;
                case R.id.tv_gallery:
                    presenter.requestReadSDCardPermission(getActivity());
                    break;
                case R.id.tv_delete_avatar:
                    deleteAvatar();
                    break;
                case R.id.tv_cancel:
                    break;
            }
        });
        if (photoFile == null && TextUtils.isEmpty(personalInfo.getAvatar())) {
            bottomSheetFragment.setAllowShowAvatar(false);
        }
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }

    private void displayAvatar() {
        String imagePath = null;
        if (photoFile != null) {
            imagePath = photoFile.getAbsolutePath();
        } else if (personalInfo != null && !TextUtils.isEmpty(personalInfo.getAvatar())) {
            imagePath = personalInfo.getAvatar();
        }

        if (imagePath != null) {
            DisplayImageProfileActivity.startActivity(this, imagePath);
        }
    }

    private void deleteAvatar() {
        if (!TextUtils.isEmpty(personalInfo.getAvatar()) || photoFile != null) {
            showNotifyDialog(getString(R.string.MH10_029), new NotifyDialog.OnNotifyCallback() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onOk(Object... obj) {
                    if (photoFile != null) {
                        photoFile = null;
                        bytes = null;
                        personalInfo.setNeedUpdateAvatar(false);
                    } else {
                        presenter.deleteImage();
                    }
                }
            });
        }
    }

    @Override
    public void onAvatarDeleted() {
        profileImage.setImageResource(R.drawable.img_default_avatar);
    }

    @Override
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = ImagesManager.createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        Constants.IMAGE_CAMERA_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onLoadBitmap(byte[] bytes) {
        this.bytes = bytes;
        setUserAvatar();
    }

    public void showSelectGenderOption() {
        GenderBottomSheet bsGender = new GenderBottomSheet();
        bsGender.setOnClickGenderOptionListener(genderSelected -> {
            selectGender.setSelectionValue(genderSelected);
            personalInfo.setGender(getGender());

        });
        bsGender.setSelectedGender(selectGender.getSelectionValue());
        bsGender.show(getChildFragmentManager(), bsGender.getTag());
    }

    public void showSelectYearOfBirthOption() {
        YearOfBirthBottomSheet bsYear = new YearOfBirthBottomSheet();

        int selectedYear = -1;
        try {
            selectedYear = Integer.parseInt(selectDob.getSelectionValue());
        } catch (NumberFormatException e) {

        }
        bsYear.setSelectedYear(selectedYear);
        bsYear.setOnYearClickListener(year -> {
            selectDob.setSelectionValue(year);
            personalInfo.setDob(getDob());
        });
        bsYear.show(getChildFragmentManager(), bsYear.getTag());
    }

    @OnClick({R.id.layout_profile_image, R.id.select_gender, R.id.select_year_of_birth,
            R.id.bt_save, R.id.bt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_image:
                showAvatarOption();
                break;

            case R.id.select_gender:
                showSelectGenderOption();
                break;

            case R.id.select_year_of_birth:
                showSelectYearOfBirthOption();
                break;

            case R.id.bt_save:
                if (isDataValid()) {
                    nextStep();
                }
                break;

            case R.id.bt_cancel:
                handleLeaveScreen();
                break;
            default:
                break;
        }
    }

    private void handleLeaveScreen() {
        allowBack = true;
        getActivity().onBackPressed();
    }

    private void nextStep() {
        switchFragment(TranslatorAddressAndLanguageFragment.newInstance(personalInfo),
                new FragmentController.Option.Builder()
                        .useAnimation(true)
                        .addToBackStack(true)
                        .setType(FragmentController.Option.TYPE.ADD)
                        .build());
    }

    private boolean isAllFieldNotChange() {
        return !isAvatarChanged()
                && oldPersonalInfo.getName().equals(getName())
                && !isIdCardNumberChanged()
                && oldPersonalInfo.getGender() == getGender()
                && !isDobChanged()
                && oldPersonalInfo.getEmail().equals(getEmail());
    }

    private boolean isAvatarChanged() {
        if (oldPersonalInfo.getAvatar() == null && getAvatar().length() > 0) {
            return true;
        } else if (!getAvatar().equals(oldPersonalInfo.getAvatar())) {
            return true;
        }
        return false;
    }

    private boolean isIdCardNumberChanged() {
        if (oldPersonalInfo.getIdentityCardNumber() == null && getPassport().length() > 0) {
            return true;
        } else if (!getPassport().equals(oldPersonalInfo.getIdentityCardNumber())) {
            return true;
        }
        return false;
    }

    private boolean isDobChanged() {
        if (oldPersonalInfo.getDob() == null && getDob().length() > 0) {
            return true;
        } else if (!getDob().equals(oldPersonalInfo.getDob())) {
            return true;
        }
        return false;
    }

    private boolean isDataValid() {

        nameValidation();
        emailValidation();
        passportValidation();
        genderAndDobValidation();
        return isAllErrorMessageGone();
    }

    private void genderAndDobValidation() {
        if (getGender() == GenderUtils.NOT_SELECTED_YET || TextUtils.isEmpty(getDob())) {
            tvGenderAndDobRequire.setText(String.format(getString(R.string.TB_1019), getString(R.string.MH09_058)));
            tvGenderAndDobRequire.setVisibility(View.VISIBLE);
        } else {
            tvGenderAndDobRequire.setVisibility(View.GONE);
        }
    }

    private boolean isAllErrorMessageGone() {
        if (fieldName.isError()) {
            ((BaseActivity) getActivity()).showKeyboard(fieldName.getEdtValue());
            return false;
        }

        if (fieldEmail.isError()) {
            ((BaseActivity) getActivity()).showKeyboard(fieldEmail.getEdtValue());
            return false;
        }

        if (fieldPassport.isError()) {
            ((BaseActivity) getActivity()).showKeyboard(fieldPassport.getEdtValue());
            return false;
        }

        if (tvGenderAndDobRequire.getVisibility() == View.VISIBLE) {
            return false;
        }

        return true;
    }

    private void emailValidation() {
        if (TextUtils.isEmpty(getEmail())) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_008)));
        } else if (!Validator.validEmail(getEmail())) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(getString(R.string.TB_1005));
        } else {
            fieldEmail.setErrorVisible(false);
        }

    }

    private void passportValidation() {
        fieldPassport.setError(TextUtils.isEmpty(getPassport()) ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH04_008)) : "");
    }

    private void nameValidation() {
        fieldName.setError(TextUtils.isEmpty(getName()) ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)) : "");
    }

    @Override
    public void backButtonClicked() {
        onViewClicked(btCancel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                presenter.loadBitmapFormFile(photoFile);
            }
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            if (imageURI != null) {
                photoFile = ImagesManager.createImageFileFromURI(getActivity(), imageURI);
                presenter.loadBitmapFormFile(photoFile);
            }
        }
    }

    @Override
    public void onDestroyView() {
        fieldName.setOnTextFieldErrorListener(null);
        fieldEmail.setOnTextFieldErrorListener(null);
        fieldPassport.setOnTextFieldErrorListener(null);
        super.onDestroyView();
    }

    private void setUserAvatar() {
        Glide.with(this)
                .load(bytes)
                .error(R.drawable.img_avatar_default)
                .placeholder(R.drawable.img_avatar_default)
                .dontAnimate()
                .transform(new CircleTransform(getActivity()))
                .into(profileImage);
        personalInfo.setAvatar(getAvatar());
        personalInfo.setNeedUpdateAvatar(true);
    }

}
