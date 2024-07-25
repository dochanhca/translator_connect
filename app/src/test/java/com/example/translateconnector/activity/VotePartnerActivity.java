package com.example.translateconnector.activity;

import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.UserNeedReview;
import com.imoktranslator.presenter.VotePartnerPresenter;
import com.imoktranslator.transform.CircleTransform;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class VotePartnerActivity extends BaseActivity implements VotePartnerPresenter.VotePartnerView {
    public static final String USER_NEED_REVIEW = "user_need_review";
    @BindView(R.id.txt_screen_title)
    TextView txtScreenTitle;
    @BindView(R.id.rating_skill)
    AppCompatRatingBar rattingSkill;

    @BindView(R.id.rating_specialize)
    AppCompatRatingBar rattingMajor;

    @BindView(R.id.rating_price)
    AppCompatRatingBar rattingPrice;

    @BindView(R.id.rating_attitude)
    AppCompatRatingBar rattingAttitude;

    @BindView(R.id.common_rating)
    AppCompatRatingBar rattingCommon;

    @BindView(R.id.edt_description)
    EditText edDescription;

    @BindView(R.id.txt_user_name)
    TextView txtUserName;
    @BindView(R.id.img_user_avatar)
    CircleImageView imgUserAvatar;
    @BindView(R.id.layout_translator_rating)
    View layoutTranslatorRating;

    private VotePartnerPresenter presenter;

    private UserNeedReview userNeedReview;
    private int currentUserId;
    private PersonalInfo personalInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_review;
    }

    @Override
    protected void initViews() {
        personalInfo = LocalSharedPreferences.getInstance(this).getPersonalInfo();
        currentUserId = personalInfo.getId();
        userNeedReview = getIntent().getParcelableExtra(USER_NEED_REVIEW);

        presenter = new VotePartnerPresenter(this, this);

        if (userNeedReview != null) {
            txtScreenTitle.setText(getString(currentUserId != userNeedReview.getUserId()
                    ? R.string.MH20_011 : R.string.MH20_012));
            layoutTranslatorRating.setVisibility(currentUserId != userNeedReview.getUserId() ?
                    View.GONE : View.VISIBLE);
            presenter.getPartnerInfo();
        }
    }

    @OnClick({R.id.bt_submit})
    public void onButtonSubmitClicked(View view) {
        if (!checkReasonRequired()) {
            presenter.votePartner();
        } else {
            showNotifyDialog(String.format(getString(R.string.TB_1001), getString(R.string.MH20_009)));
            edDescription.requestFocus();
        }
    }

    private boolean checkReasonRequired() {
        return (personalInfo.isTranslator() ?
                rattingCommon.getRating() <= 2 :
                (rattingCommon.getRating() <= 2 || rattingAttitude.getRating() <= 2
                        || rattingMajor.getRating() <= 2 || rattingPrice.getRating() <= 2
                        || rattingSkill.getRating() <= 2)) && edDescription.getText().toString().isEmpty();
    }

    @OnClick(R.id.img_user_avatar)
    public void onAvatarClicked(View view) {
        RatingDetailActivity.startActivity(this, getPartnerId());
    }

    @Override
    public int getUserId() {
        return currentUserId;
    }

    @Override
    public int getPartnerId() {
        if (currentUserId == userNeedReview.getUserId()) {
            return userNeedReview.getAcceptedTranslatorId();
        } else {
            return userNeedReview.getUserId();
        }
    }

    @Override
    public float getTotalRatting() {
        return rattingCommon.getRating();
    }

    @Override
    public String getContent() {
        return edDescription.getText().toString().trim();
    }

    @Override
    public float getAttitudeRatting() {
        return rattingAttitude.getRating();
    }

    @Override
    public float getSkillRatting() {
        return rattingSkill.getRating();
    }

    @Override
    public float getMajorRatting() {
        return rattingMajor.getRating();
    }

    @Override
    public float getPriceRatting() {
        return rattingPrice.getRating();
    }

    @Override
    public void voteSuccessful() {
        this.finish();
    }

    @Override
    public int getOrderId() {
        return userNeedReview.getOrderId();
    }

    @Override
    public void getPartnerInfoSuccessful(PersonalInfo personalInfo) {

        if (!TextUtils.isEmpty(personalInfo.getName())) {
            txtUserName.setText(Utils.hideStringWithStars(personalInfo.getName()));
        }

        if (!TextUtils.isEmpty(personalInfo.getAvatar())) {
            Glide.with(this)
                    .load(personalInfo.getAvatar())
                    .error(R.drawable.img_avatar_default)
                    .transform(new CircleTransform(this))
                    .into(imgUserAvatar);
        }

    }

    @Override
    public void onBackPressed() {
        //disable back
    }
}
