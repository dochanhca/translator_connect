package com.example.translateconnector.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.imoktranslator.R;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.utils.Constants;

import java.util.List;

public class SignUpTranslatorStep2Presenter extends BasePresenter {
    private SignUpTranslatorStep2View view;

    public SignUpTranslatorStep2Presenter(Context context, SignUpTranslatorStep2View view) {
        super(context);
        this.view = view;
    }

    public void onNextStepClicked() {
        boolean isLocationValid = locationValidation();
        boolean isNativeLanguageValid = nativeLanguageValidation();
        boolean isTranslationLanguageValid = translationLanguageValidation();
        if (isLocationValid && isNativeLanguageValid && isTranslationLanguageValid) {
            view.goToNextStep();
        }
    }

    private boolean translationLanguageValidation() {
        List<String> translationLanguageList = view.getTranslationLanguages();
        if (translationLanguageList.size() == 0) {
            view.showTranslationLanguageIsRequireWarning();
            return false;
        } else {
            view.hideTranslationLanguageIsRequireWarning();
            return true;
        }
    }

    private boolean nativeLanguageValidation() {
        int selectedNativeLanguage = view.getIndexOfSelectedNativeLanguage();
        if (selectedNativeLanguage < 1) {
            view.showNativeLanguageIsRequireWarning();
            return false;
        } else {
            view.hideNativeLanguageIsRequireWarning();
            return true;
        }
    }

    private boolean locationValidation() {
        String place = view.getPlaceSelectedByPlacePicker();
        String selectOnMapPlaceHolder = view.getSelectOnMapPlaceHolderValue();
        String selectedCountry = view.getSelectedCountry();
        if (place.equals(selectOnMapPlaceHolder) && TextUtils.isEmpty(selectedCountry)) {
            view.showLocationIsRequireWarning();
            return false;
        } else {
            view.hideLocationIsRequireWarning();
            return true;
        }
    }

    public void onCancelClicked(PersonalInfo personalInfo) {

        boolean isCountryNotChange = !isCountryChanged(personalInfo);
        boolean isCityNotChange = !isCityChanged(personalInfo);
        boolean isAddressNotChange = !isAddressChanged(personalInfo);
        boolean isNativeLanguageNotChange = personalInfo.getForeignLanguages() == view.getIndexOfSelectedNativeLanguage();

        boolean isListTranslationNotChange = !isTransLanguageChanged(personalInfo);

        if (isCountryNotChange && isCityNotChange && isAddressNotChange
                && isNativeLanguageNotChange && isListTranslationNotChange) {
            view.backToPreScreen();
        } else {
            view.showNoticeDataChanged();
        }
    }

    private boolean isTransLanguageChanged(PersonalInfo personalInfo) {
        List<String> listTransLang = personalInfo.getTranslateLanguages();
        List<String> newListTransLang = view.getTranslationLanguages();

        if (listTransLang == null) {
            return newListTransLang.size() == 0 ? false : true;
        } else if (listTransLang.size() != newListTransLang.size()) {
            return true;
        } else {
            for (int i = 0; i < listTransLang.size(); i++) {
                if (!listTransLang.get(i).equals(newListTransLang.get(i))) {
                    return true;
                }
            }

        }

        return false;
    }

    private boolean isAddressChanged(PersonalInfo personalInfo) {

        if (personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            if (personalInfo.getAddress() == null && view.getAddress().isEmpty()) {
                return false;
            } else if (view.getAddress().equals(personalInfo.getAddress())) {
                return false;
            }
            return true;
        } else {
            if (personalInfo.getAddress() == null &&
                    view.getPlaceSelectedByPlacePicker().equals(getContext().getString(R.string.MH11_006))) {
                return false;
            } else if (view.getAddress().equals(view.getPlaceSelectedByPlacePicker())) {
                return false;
            }
            return true;
        }
    }

    private boolean isCityChanged(PersonalInfo personalInfo) {
        if (personalInfo.getCity() == null && view.getSelectedCity().isEmpty()) {
            return false;
        } else if (view.getSelectedCity().equals(personalInfo.getCity())) {
            return false;
        }
        return true;
    }

    private boolean isCountryChanged(PersonalInfo personalInfo) {
        if (personalInfo.getCountry() == null && view.getSelectedCountry().isEmpty()) {
            return false;
        } else if (view.getSelectedCountry().equals(personalInfo.getCountry()
        )) {
            return false;
        }
        return true;
    }


    public interface SignUpTranslatorStep2View extends BaseView {

        String getPlaceSelectedByPlacePicker();

        String getSelectOnMapPlaceHolderValue();

        String getSelectedCountry();

        void showLocationIsRequireWarning();

        void hideLocationIsRequireWarning();

        int getIndexOfSelectedNativeLanguage();

        void showNativeLanguageIsRequireWarning();

        void hideNativeLanguageIsRequireWarning();

        List<String> getTranslationLanguages();

        void showTranslationLanguageIsRequireWarning();

        void hideTranslationLanguageIsRequireWarning();

        void goToNextStep();

        String getLatitude();

        String getLongitude();

        String getSelectedCity();

        String getAddress();

        void backToPreScreen();

        void showNoticeDataChanged();
    }
}
