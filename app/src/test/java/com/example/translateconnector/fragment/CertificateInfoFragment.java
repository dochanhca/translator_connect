package com.example.translateconnector.fragment;

import android.Manifest;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.R;
import com.imoktranslator.activity.SignUpTranslatorActivity;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.ListTagView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.utils.Constants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CertificateInfoFragment extends BaseFragment implements HeaderView.BackButtonClickListener {

    @BindView(R.id.field_certificate)
    TextFieldView fieldCertificate;
    @BindView(R.id.list_certificate)
    ListTagView ltvCertificate;
    @BindView(R.id.selection_experience)
    SelectionView selectExperience;
    @BindView(R.id.select_graduation_year)
    SelectionView selectGraduationYear;
    @BindView(R.id.select_degree_classification)
    SelectionView selectDegreeClassification;
    @BindView(R.id.field_university)
    TextFieldView fieldUniversity;
    @BindView(R.id.tv_file_number)
    TextView tvFileNumber;
    @BindView(R.id.tv_file_attach_require)
    TextView tvFileAttachRequire;
    @BindView(R.id.field_other_info)
    TextFieldView fieldOtherInfo;
    @BindView(R.id.header_certificate_info)
    HeaderView headerCertificateInfo;
    @BindView(R.id.bt_add_certificate)
    TextView btAddCertificate;
    @BindView(R.id.bt_upload_certificate)
    TextView btnUploadCertificate;

    private List<String> listExperience;
    private List<String> listGraduationType;
    private List<String> listGraduationYear;
    private int fileCount;

    private PersonalInfo personalInfo;

    public static CertificateInfoFragment newInstance(PersonalInfo personalInfo) {

        Bundle args = new Bundle();

        CertificateInfoFragment fragment = new CertificateInfoFragment();
        fragment.setArguments(args);
        fragment.personalInfo = personalInfo;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_certificate_info;
    }

    @Override
    protected void initViews() {

        headerCertificateInfo.setCallback(this);
        headerCertificateInfo.setTittle(getString(R.string.MH09_026));
        initDataForBottomSheets();

        tvFileNumber.setTypeface(tvFileNumber.getTypeface(), Typeface.ITALIC);
        fieldCertificate.getEdtValue().setImeOptions(EditorInfo.IME_ACTION_DONE);
        fieldOtherInfo.getEdtValue().setImeOptions(EditorInfo.IME_ACTION_DONE);
        fieldUniversity.setOnTextFieldErrorListener(this::universityValidation);

        ltvCertificate.setOnListStateListener(new ListTagView.OnListStateListener() {
            @Override
            public void onAdded(String tagName) {
                if (ltvCertificate.getDataSet().size() == 9) {
                    btAddCertificate.setVisibility(View.GONE);
                }
                personalInfo.setCertificateName(ltvCertificate.getDataSet());
            }

            @Override
            public void onRemoved(String tagName) {
                if (ltvCertificate.getDataSet().size() < 9) {
                    btAddCertificate.setVisibility(View.VISIBLE);
                }
                personalInfo.setCertificateName(ltvCertificate.getDataSet());
            }

            @Override
            public void onFullSlot() {

            }
        });

        setFileCount(personalInfo.getAttachPaths() != null ? getAttachedFileCount() : 0);

        fillDataToView();
        initDataChangedListener();
    }

    private int getAttachedFileCount() {
        int count = 0;
        for (Image image : personalInfo.getAttachPaths()) {
            if (image != null && !TextUtils.isEmpty(image.path)) {
                count++;
            }
        }
        return count;
    }

    public void setFileCount(int count) {
        fileCount = count;
        tvFileNumber.setText(String.format(getString(R.string.MH09_021), String.valueOf(fileCount)));
        tvFileAttachRequire.setVisibility(count == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void initDataChangedListener() {
        fieldUniversity.setOnDataChangedListener(data -> personalInfo.setUniversity(data));
        fieldOtherInfo.setOnDataChangedListener(data -> personalInfo.setOtherInfo(data));
    }

    private void fillDataToView() {
        fieldUniversity.setText(personalInfo.getUniversity());
        if (personalInfo.getDegreeClassification() > 0) {
            selectDegreeClassification.setSelectionValue(listGraduationType.get(personalInfo.getDegreeClassification() - 1));
        }

        selectGraduationYear.setSelectionValue(personalInfo.getYearOfGraduation());

        if (personalInfo.getYearExperience() > 0) {
            selectExperience.setSelectionValue(listExperience.get(personalInfo.getYearExperience() - 1));
        }

        List<String> certificateList = personalInfo.getCertificateName();
        if (certificateList != null && certificateList.size() > 0) {
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

    private void initDataForBottomSheets() {
        listGraduationType = Arrays.asList(getResources().getStringArray(R.array.graduation_type));

        listGraduationYear = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1950; i--) {
            listGraduationYear.add(String.valueOf(i));
        }

        listExperience = Arrays.asList(getResources().getStringArray(R.array.arr_experience));
    }

    @OnClick({R.id.select_degree_classification, R.id.select_graduation_year, R.id.bt_upload_certificate,
            R.id.bt_add_certificate, R.id.selection_experience, R.id.bt_done, R.id.bt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_degree_classification:
                showSelectGraduationType();
                break;
            case R.id.select_graduation_year:
                showSelectGraduationYear();
                break;
            case R.id.bt_upload_certificate:
                requestReadSdCard();
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
            case R.id.bt_done:
                if (isDataValid()) {
                    ((SignUpTranslatorActivity) getActivity()).signUpTranslator();
                }
                break;
            case R.id.bt_cancel:
                backButtonClicked();
                break;
        }
    }

    private void requestReadSdCard() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        showDialogConfirm();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showDialogConfirm() {
        showNotifyDialog(getString(R.string.TB_1041), new NotifyDialog.OnNotifyCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(Object... obj) {
                openAttachFileScreen();
            }
        });
    }

    private void openAttachFileScreen() {
        switchFragment(AttachFileFragment.newInstance(personalInfo),
                new FragmentController.Option.Builder()
                        .setType(FragmentController.Option.TYPE.ADD)
                        .build());
    }

    private boolean isDataValid() {
        universityValidation(getUniversity());
        fileAttachValidation();
        return isAllErrorGone();
    }

    private boolean isAllErrorGone() {
        return !fieldUniversity.isError() && tvFileAttachRequire.getVisibility() != View.VISIBLE;
    }

    private void fileAttachValidation() {
        if (fileCount > 0) {
            tvFileAttachRequire.setVisibility(View.INVISIBLE);
        } else {
            tvFileAttachRequire.setText(String.format(getString(R.string.TB_1001), getString(R.string.MH09_036)));
            tvFileAttachRequire.setVisibility(View.VISIBLE);
        }
    }

    private void universityValidation(String university) {
        if (fieldUniversity != null) {
            if (TextUtils.isEmpty(university)) {
                fieldUniversity.setErrorVisible(true);
                fieldUniversity.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH09_013)));
            } else {
                fieldUniversity.setErrorVisible(false);
            }
        }
    }

    private String getUniversity() {
        return fieldUniversity.getText();
    }

    private void showSelectExperience() {
        CustomBottomSheetFragment bsExperience = CustomBottomSheetFragment.newInstance(getString(R.string.MH11_016));
        bsExperience.setOptions(listExperience);
        bsExperience.setSelectedPosition(personalInfo.getYearExperience() - 1);
        bsExperience.setListener(position -> {
            selectExperience.setSelectionValue(listExperience.get(position));
            personalInfo.setYearExperience(position + 1);
        });
        bsExperience.show(getChildFragmentManager(), bsExperience.getTag());
    }

    private String getLastCertificateValue() {
        return fieldCertificate.getText();
    }

    private void showSelectGraduationYear() {
        CustomBottomSheetFragment graduationYears = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_017));
        graduationYears.setOptions(listGraduationYear);
        graduationYears.setSelectedPosition(-1);
        graduationYears.setListener(position -> {
            selectGraduationYear.setSelectionValue(listGraduationYear.get(position));
            personalInfo.setYearOfGraduation(listGraduationYear.get(position));
        });
        graduationYears.show(getChildFragmentManager(), graduationYears.getTag());
    }

    private void showSelectGraduationType() {
        CustomBottomSheetFragment graduationTypes = CustomBottomSheetFragment.newInstance(getString(R.string.MH09_016));
        graduationTypes.setOptions(listGraduationType);
        graduationTypes.setSelectedPosition(personalInfo.getDegreeClassification() - 1);
        graduationTypes.setListener(position -> {
            selectDegreeClassification.setSelectionValue(listGraduationType.get(position));
            personalInfo.setDegreeClassification(position + 1);
        });
        graduationTypes.show(getChildFragmentManager(), graduationTypes.getTag());
    }

    @Override
    public boolean isHandleBackPressedOnFragment() {
        return false;
    }

    @Override
    public void backButtonClicked() {
        getFragmentManager().popBackStackImmediate();
    }
}
