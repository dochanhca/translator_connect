package com.example.translateconnector.activity;

import android.content.Intent;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.adapter.ListCertificateAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansSemiBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.presenter.UserDetailPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends BaseActivity implements UserDetailPresenter.UserDetailView {

    @BindView(R.id.header_user_info)
    HeaderView headerUserInfo;
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.txt_order_statistic)
    OpenSansSemiBoldTextView txtOrderStatistic;
    @BindView(R.id.txt_success_order)
    OpenSansSemiBoldTextView txtSuccessOrder;
    @BindView(R.id.txt_updated_price_order)
    OpenSansSemiBoldTextView txtUpdatedPriceOrder;
    @BindView(R.id.txt_quality_label)
    OpenSansBoldTextView txtQualityLabel;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.txt_name)
    OpenSansBoldTextView txtName;
    @BindView(R.id.layout_name)
    LinearLayout layoutName;
    @BindView(R.id.txt_address)
    OpenSansBoldTextView txtAddress;
    @BindView(R.id.txt_dob)
    OpenSansBoldTextView txtDob;
    @BindView(R.id.txt_primary_lang)
    OpenSansBoldTextView txtPrimaryLang;
    @BindView(R.id.txt_translation_lang)
    OpenSansBoldTextView txtTranslationLang;
    @BindView(R.id.txt_university)
    OpenSansBoldTextView txtUniversity;
    @BindView(R.id.txt_gradation_year)
    OpenSansBoldTextView txtGradationYear;
    @BindView(R.id.layout_gradation_year)
    LinearLayout layoutGradationYear;
    @BindView(R.id.txt_certificate_label)
    OpenSansBoldTextView txtCertificateLabel;
    @BindView(R.id.rcv_certificate)
    RecyclerView rcvCertificate;
    @BindView(R.id.txt_description_label)
    OpenSansBoldTextView txtDescriptionLabel;
    @BindView(R.id.txt_description)
    OpenSansTextView txtDescription;
    @BindView(R.id.layout_translator_detail)
    ViewGroup layoutTranslatorDetail;

    private int userId;
    private UserDetailPresenter presenter;
    private ListCertificateAdapter listCertificateAdapter;

    private String[] languages;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initViews() {
        presenter = new UserDetailPresenter(this, this);
        userId = getIntent().getIntExtra(Constants.USER_ID, -1);
        languages = getResources().getStringArray(R.array.arr_language);

        headerUserInfo.setCallback(() -> onBackPressed());

        initRecyclerCertificate();
        presenter.getUserDetail(userId);

    }

    @Override
    public void onGetUserDetail(PersonalInfo personalInfo) {
        fillDataToViews(personalInfo);
    }

    private void fillDataToViews(PersonalInfo personalInfo) {
        headerUserInfo.setTittle(getString(personalInfo.isTranslator()
                ? R.string.MH13_010 : R.string.MH63_007));
        Glide.with(this)
                .load(personalInfo.getAvatar())
                .error(R.drawable.img_avatar_default)
                .into(imgAvatar);

        ratingBar.setEnabled(false);
        ratingBar.setRating((float) personalInfo.getScore());

        String successOrder = personalInfo.isTranslator() ?
                getString(R.string.MH13_003) + ": " + personalInfo.getOrderSuccess()
                : getString(R.string.MH42_001) + ": " + personalInfo.getOrderCreated();
        txtSuccessOrder.setText(successOrder);

        String canceledOrder = personalInfo.isTranslator() ?
                getString(R.string.MH13_002) + ": " + personalInfo.getOrderUpdatedPrice()
                : getString(R.string.MH43_001) + ": " + personalInfo.getOrderCanceled();
        txtUpdatedPriceOrder.setText(canceledOrder);


        txtName.setText(Utils.hideStringWithStars(personalInfo.getName()));
        txtAddress.setText(getAddress(personalInfo));
        txtDob.setText(personalInfo.getDob());

        layoutTranslatorDetail.setVisibility(personalInfo.isTranslator() ? View.VISIBLE : View.GONE);
        if (personalInfo.isTranslator()) {
            fillDataForTranslator(personalInfo);
        }
    }

    private void fillDataForTranslator(PersonalInfo personalInfo) {
        headerUserInfo.setTittle(getString(personalInfo.isTranslator()
                ? R.string.MH13_010 : R.string.MH63_007));

        if (personalInfo.getCertificateName() != null) {
            listCertificateAdapter.setData(personalInfo.getCertificateName());
        }
        txtPrimaryLang.setText(languages[personalInfo.getForeignLanguages() - 1]);
        StringBuilder transLanguage = new StringBuilder();
        for (String lang : personalInfo.getTranslateLanguages()) {
            transLanguage.append(languages[Integer.parseInt(lang) - 1]).append(", ");
        }
        txtTranslationLang.setText(transLanguage.toString());
        txtUniversity.setText(personalInfo.getUniversity());
        txtGradationYear.setText(personalInfo.getYearOfGraduation());
        txtDescription.setText(personalInfo.getOtherInfo());

        txtCertificateLabel.append(":");
        txtDescriptionLabel.append(":");
    }

    private String getAddress(PersonalInfo personalInfo) {
        String address;
        if (personalInfo.getAddressType() == Constants.ADDRESS_TYPE_FILTER) {
            address = TextUtils.isEmpty(personalInfo.getCity()) ? personalInfo.getCountry() :
                    personalInfo.getCity() + ", " + personalInfo.getCountry();
        } else {
            address = personalInfo.getAddress();
        }
        return address;
    }

    private void initRecyclerCertificate() {
        listCertificateAdapter = new ListCertificateAdapter(new ArrayList<>(), getApplicationContext());
        rcvCertificate.setAdapter(listCertificateAdapter);
    }

    public static void startActivity(BaseActivity baseActivity, int userId) {
        Intent intent = new Intent(baseActivity, UserDetailActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        baseActivity.startActivity(intent);
    }
}
