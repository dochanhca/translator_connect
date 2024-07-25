package com.example.translateconnector.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.fragment.BaseFragment;
import com.imoktranslator.fragment.FragmentBox;
import com.imoktranslator.fragment.FragmentController;
import com.imoktranslator.fragment.TranslatorBasicInfoFragment;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.presenter.BaseProfileActivity;
import com.imoktranslator.presenter.SignUpTranslatorPresenter;
import com.imoktranslator.service.HandleCacheDataService;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tvoer on 4/5/18.
 */

public class SignUpTranslatorActivity extends BaseActivity implements FragmentBox,
        SignUpTranslatorPresenter.SignUpTranslatorView {
    public static final String KEY_PERSONAL_INFO_OBJ = "key_personal_info_obj";
    private PersonalInfo personalInfo;
    private FragmentController fm;

    private SignUpTranslatorPresenter presenter;
    private boolean isBackPressed;

    private String TAG = getClass().getSimpleName();

    private BroadcastReceiver shutdownReceiver = new ShutdownReceiver();

    @Override
    public int getContainerViewId() {
        return R.id.sign_up_translator_container;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up_translator;
    }

    @Override
    protected void initViews() {
        presenter = new SignUpTranslatorPresenter(this, this);
        fm = new FragmentController(getSupportFragmentManager(), getContainerViewId());
        personalInfo = getIntent().getParcelableExtra(KEY_PERSONAL_INFO_OBJ);
        if (LocalSharedPreferences.getInstance(this).getCachedPersonalInfo() != null) {
            personalInfo = LocalSharedPreferences.getInstance(this).getCachedPersonalInfo();
        }

        registerShutdownReceiver();
        startService();

        TranslatorBasicInfoFragment basicInfoFragment = TranslatorBasicInfoFragment.newInstance(personalInfo);
        FragmentController.Option option = new FragmentController.Option.Builder()
                .useAnimation(false)
                .addToBackStack(false)
                .setType(FragmentController.Option.TYPE.ADD)
                .build();
        switchFragment(basicInfoFragment, option);
    }

    private void registerShutdownReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, intentFilter);
    }

    @Override
    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        if (fm != null && baseFragment != null && option != null) {
            fm.switchFragment(baseFragment, option);
        }
    }

    @Override
    public void onDestroy() {
        if (!isBackPressed) {
            //App being killed by recent button
            LocalSharedPreferences.getInstance(this).saveCachePersonalInfo(personalInfo);
        }
        HandleCacheDataService.stopService(this);
        unregisterReceiver(shutdownReceiver);
        super.onDestroy();
    }

    private void startService() {
        HandleCacheDataService.startService(this);
    }

    public void signUpTranslator() {
        PersonalInfoParam param = new PersonalInfoParam();
        param.setName(personalInfo.getName());
        param.setEmail(personalInfo.getEmail());
        param.setPhone(personalInfo.getPhone());
        param.setGender(personalInfo.getGender());
        param.setDob(personalInfo.getDob());
        param.setPassport(personalInfo.getIdentityCardNumber());


        int addressType = Constants.ADDRESS_TYPE_MAP;
        if (TextUtils.isEmpty(personalInfo.getLatitude()) && TextUtils.isEmpty(personalInfo.getLongitude())) {
            addressType = Constants.ADDRESS_TYPE_FILTER;
        }
        param.setAddressType(addressType);
        String address = personalInfo.getAddress();

        param.setAddress(address);
        param.setCountry(personalInfo.getCountry());
        param.setCity(personalInfo.getCity());
        param.setLat(personalInfo.getLatitude());
        param.setLon(personalInfo.getLongitude());
        param.setNativeLanguage(personalInfo.getForeignLanguages());

        List<String> tranIndexes = new ArrayList<>();
        int tranLanguageIndex;
        for (String language : personalInfo.getTranslateLanguages()) {
            tranLanguageIndex = Integer.parseInt(language);
            tranIndexes.add(String.valueOf(tranLanguageIndex));
        }
        param.setTransLanguage(tranIndexes);


        param.setUniversity(personalInfo.getUniversity());
        param.setDegreeClassification(personalInfo.getDegreeClassification());
        param.setGraduationYear(personalInfo.getYearOfGraduation());
        param.setExperience(personalInfo.getYearExperience());
        param.setOtherInfo(personalInfo.getOtherInfo());
        param.setCertificates(personalInfo.getCertificateName());
        param.setNeedUpdateAvatar(personalInfo.isNeedUpdateAvatar());

        presenter.signUpTranslator(param);
    }

    @Override
    public File getProfileImage() {
        return new File(personalInfo.getAvatar());
    }

    @Override
    public void backToUserProfileScreen(PersonalInfo personalInfo) {
        Toast.makeText(this, getString(R.string.TB_2010), Toast.LENGTH_LONG).show();

        isBackPressed = true;

        Intent resultIntent = new Intent();
        resultIntent.putExtra(BaseProfileActivity.KEY_PERSONAL_INFO, personalInfo);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public List<Image> getImages() {
        return personalInfo.getAttachPaths();
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard();
        }
        return super.dispatchTouchEvent(ev);
    }



    public class ShutdownReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Insert code here
            Log.e(TAG, "Device Power off");
            LocalSharedPreferences.getInstance(SignUpTranslatorActivity.this).saveCachePersonalInfo(personalInfo);
        }
    }
}
