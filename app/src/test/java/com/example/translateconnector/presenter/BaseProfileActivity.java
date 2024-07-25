package com.example.translateconnector.presenter;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.imoktranslator.R;
import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.activity.ChangePasswordActivity;
import com.imoktranslator.activity.DisplayImageProfileActivity;
import com.imoktranslator.bottomsheet.AvatarOptionBottomSheet;
import com.imoktranslator.bottomsheet.CountryBottomSheet;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.bottomsheet.ProvinceBottomSheet;
import com.imoktranslator.bottomsheet.YearOfBirthBottomSheet;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.ProvinceModel;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.ImagesManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseProfileActivity extends BaseActivity implements BaseProfilePresenter.BaseProfileView {

    private static final String TAG = BaseProfileActivity.class.getSimpleName();
    protected static final int REQUEST_CODE_SELECT_ON_MAP = 69;
    protected static final int REQUEST_SIGN_UP_TRANSLATOR = 96;
    protected static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static final int REQUEST_IMAGE_GALLERY = 2;

    public static final String KEY_PERSONAL_INFO = "key_personal_info";
    protected static final String ROLE_USER = "user";
    protected static final String ROLE_TRANSLATOR = "translator";

    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;
    @BindView(R.id.select_place)
    protected SelectionView selectOnMap;
    @BindView(R.id.select_country)
    protected SelectionView selectCountry;
    @BindView(R.id.select_city)
    protected SelectionView selectCity;
    @BindView(R.id.field_address)
    protected TextFieldView fieldAddress;

    protected BaseProfilePresenter presenter;
    protected PersonalInfo personalInfo;
    protected File photoFile;
    protected byte[] bytes;
    protected String longitude;
    protected String latitude;
    protected List<String> genders;
    protected List<String> listLanguage;

    @Override
    protected void initViews() {
        presenter = new BaseProfilePresenter(this, this);
        personalInfo = getIntent().getParcelableExtra(KEY_PERSONAL_INFO);
        genders = convertArrToListString(R.array.arr_gender);
        String[] arrLanguage = getResources().getStringArray(R.array.arr_language);
        listLanguage = Arrays.asList(arrLanguage);
    }

    protected void openChangePasswordScreen() {
        ChangePasswordActivity.startActivity(this, -1, false);
    }

    protected void openMap() {
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

    protected void showSelectCountryOption(String selectedCountry) {
        CountryBottomSheet bsCountry = new CountryBottomSheet();
        bsCountry.setSelectedCountry(selectedCountry);
        bsCountry.setOnCountryClickListener(this::onCountrySelected);
        bsCountry.show(getSupportFragmentManager(), bsCountry.getTag());
    }

    protected void showSelectProvinceOption(List<ProvinceModel> provinceList, String selectedProvince) {
        if (provinceList.size() > 0) {
            ProvinceBottomSheet bsProvince = new ProvinceBottomSheet();
            bsProvince.setProvinceList(provinceList);
            bsProvince.setSelectedProvince(selectedProvince);
            bsProvince.setOnProvinceClickListener(this::onCitySelected);
            bsProvince.show(getSupportFragmentManager(), bsProvince.getTag());
        }
    }

    protected void showSelectYearOfBirthOption(int selectedYear) {
        YearOfBirthBottomSheet bsYear = new YearOfBirthBottomSheet();
        bsYear.setSelectedYear(selectedYear);
        bsYear.setOnYearClickListener(this::onDOBSelected);
        bsYear.show(getSupportFragmentManager(), bsYear.getTag());
    }

    protected void showSelectGender(int selectedPosition) {
        CustomBottomSheetFragment bottomSheetFragment = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_018));
        bottomSheetFragment.setOptions(genders);
        bottomSheetFragment.setSelectedPosition(selectedPosition);
        bottomSheetFragment.setListener(position -> onGenderSelected(position));
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    protected int getSelectedPosition(List<String> selectionList, String value) {
        return TextUtils.isEmpty(value) ? -1 : selectionList.indexOf(value);
    }

    protected void showAvatarOption() {
        AvatarOptionBottomSheet bottomSheetFragment = new AvatarOptionBottomSheet();
        bottomSheetFragment.setOnOptionClickListener(view -> {
            switch (view.getId()) {
                case R.id.tv_display_avatar:
                    String imagePath = null;
                    if (photoFile != null) {
                        imagePath = photoFile.getAbsolutePath();
                    } else if (personalInfo != null && !TextUtils.isEmpty(personalInfo.getAvatar())) {
                        imagePath = personalInfo.getAvatar();
                    }

                    if (imagePath != null) {
                        DisplayImageProfileActivity.startActivity(this, imagePath);
                    }
                    break;

                case R.id.tv_capture_avatar:
                    presenter.requestCameraPermission(this);
                    break;

                case R.id.tv_gallery:
                    presenter.requestReadSDCardPermission(this);
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
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    protected boolean isAddingAvatar() {
        return photoFile != null;
    }

    protected void setAddressToDefault() {
        fieldAddress.setText("");
    }

    protected void setCityToDefault() {
        selectCity.setSelectionValue("");
    }

    protected void setCountryToDefault() {
        selectCountry.setSelectionValue("");
    }

    private void deleteAvatar() {
        if (!TextUtils.isEmpty(personalInfo.getAvatar())) {
            showNotifyDialog(getString(R.string.MH10_029), new NotifyDialog.OnNotifyCallback() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onOk(Object... obj) {
                    presenter.deleteImage();
                }
            });
        } else if (photoFile != null) {
            photoFile = null;
            bytes = null;
            onAvatarDeleted();
        }
    }

    @Override
    public void onDeleteAvatar(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        onAvatarDeleted();
    }

    @Override
    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = ImagesManager.createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
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
    public void onBackPressed() {
        if (checkDataChanged() || isAddingAvatar()) {
            showNotifyDialog(getString(R.string.MH10_031), new NotifyDialog.OnNotifyCallback() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onOk(Object... obj) {
                    finish();
                }
            });
        } else {
            navigateBack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ON_MAP && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            latitude = String.valueOf(place.getLatLng().latitude);
            longitude = String.valueOf(place.getLatLng().longitude);
            selectOnMap.setSelectionValue(place.getAddress().toString());
            setCountryToDefault();
            setCityToDefault();
            setAddressToDefault();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                presenter.loadBitmapFormFile(photoFile);

            }
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            if (imageURI != null) {
                photoFile = ImagesManager.getFileFromUri(this, imageURI);
                presenter.loadBitmapFormFile(photoFile);
            }
        }
    }

    @Override
    public void onLoadBitmap(byte[] bytes) {
        this.bytes = bytes;
        loadUserAvatar();
    }

    @Override
    public String getFileName() {
        return photoFile.getName();
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public void updateProfileInvalid() {
        Toast.makeText(this,getString(R.string.TB_2099) , Toast.LENGTH_LONG).show();
    }

    private void loadUserAvatar() {
        Glide.with(this)
                .load(bytes)
                .asBitmap()
                .error(R.drawable.img_avatar_default)
                .placeholder(R.drawable.img_avatar_default)
                .dontAnimate()
                .transform(new CircleTransform(this))
                .into(profileImage);
    }

    private void navigateBack() {
        Intent intent = new Intent();
        intent.putExtra(KEY_PERSONAL_INFO, personalInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected abstract boolean checkDataChanged();

    protected abstract void onDOBSelected(String year);

    protected abstract void onCountrySelected(String countryName);

    protected abstract void onAvatarDeleted();

    protected abstract void onGenderSelected(int position);

    protected abstract void onCitySelected(String provinceName);
}
