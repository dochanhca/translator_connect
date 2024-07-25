package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.imoktranslator.R;
import com.imoktranslator.adapter.UserReviewAdapter;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.model.RatingDetail;
import com.imoktranslator.model.ReviewContent;
import com.imoktranslator.presenter.RatingDetailPresenter;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;

public class RatingDetailActivity extends BaseActivity implements RatingDetailPresenter.RatingDetailView {

    private static final String USER_ID = "USER_ID";

    @BindView(R.id.header_user_review)
    HeaderView headerUserReview;
    @BindView(R.id.rating_common)
    AppCompatRatingBar ratingCommon;
    @BindView(R.id.rating_skill)
    AppCompatRatingBar ratingSkill;
    @BindView(R.id.rating_specialize)
    AppCompatRatingBar ratingSpecialize;
    @BindView(R.id.rating_price)
    AppCompatRatingBar ratingPrice;
    @BindView(R.id.rating_attitude)
    AppCompatRatingBar ratingAttitude;
    @BindView(R.id.layout_translator_rating)
    LinearLayout layoutTranslatorRating;
    @BindView(R.id.rcv_comment)
    RecyclerView rcvComment;
    @BindView(R.id.txt_skill_label)
    OpenSansBoldTextView txtSkillLabel;
    @BindView(R.id.txt_specialize_label)
    OpenSansBoldTextView txtSpecializeLabel;
    @BindView(R.id.txt_price_label)
    OpenSansBoldTextView txtPriceLabel;
    @BindView(R.id.txt_attitude_label)
    OpenSansBoldTextView txtAttitudeLabel;
    @BindView(R.id.txt_comment_label)
    OpenSansBoldTextView txtCommentLabel;

    private UserReviewAdapter userReviewAdapter;
    private RatingDetailPresenter presenter;
    private boolean isRatingForUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rating_detail;
    }

    @Override
    protected void initViews() {
        headerUserReview.setTittle(getString(R.string.MH20_010));
        headerUserReview.setCallback(() -> onBackPressed());

        userReviewAdapter = new UserReviewAdapter(new ArrayList<>(), getApplicationContext());
        rcvComment.setAdapter(userReviewAdapter);

        ratingCommon.setEnabled(false);
        ratingSkill.setEnabled(false);
        ratingSpecialize.setEnabled(false);
        ratingPrice.setEnabled(false);
        ratingAttitude.setEnabled(false);

        txtSkillLabel.setText(getString(R.string.MH99_004).toUpperCase());
        txtSpecializeLabel.setText(getString(R.string.MH99_005).toUpperCase());
        txtPriceLabel.setText(getString(R.string.MH99_003).toUpperCase());
        txtAttitudeLabel.setText(getString(R.string.MH99_002).toUpperCase());
        txtCommentLabel.setText(getString(R.string.MH20_009).toUpperCase());

        txtSkillLabel.append(":");
        txtSpecializeLabel.append(":");
        txtPriceLabel.append(":");
        txtAttitudeLabel.append(":");
        txtCommentLabel.append(":");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RatingDetailPresenter(this, this);
        int userId = getIntent().getIntExtra(USER_ID, -1);
        if (userId != -1) {
            presenter.getRatingDetail(userId);
        }
    }

    @Override
    public void getRatingDetailSuccessful(RatingDetail ratingDetail) {
        isRatingForUser = ratingDetail.isRattingForUser();
        layoutTranslatorRating.setVisibility(isRatingForUser ? View.GONE : View.VISIBLE);

        Iterator<ReviewContent> itr = ratingDetail.getReviews().iterator();
        while (itr.hasNext()) {
            if (TextUtils.isEmpty(itr.next().getContent())) {
                itr.remove();
            }
        }
        userReviewAdapter.setData(ratingDetail.getReviews());

        setRatingValues(ratingDetail);
    }

    private void setRatingValues(RatingDetail ratingDetail) {
        ratingCommon.setRating(ratingDetail.getTotalRatting() == null ? 0 : ratingDetail.getTotalRatting());
        ratingSkill.setRating(ratingDetail.getSkillRatting() == null ? 0 : ratingDetail.getSkillRatting());
        ratingSpecialize.setRating(ratingDetail.getMajorRatting() == null ? 0 : ratingDetail.getMajorRatting());
        ratingPrice.setRating(ratingDetail.getPriceRatting() == null ? 0 : ratingDetail.getPriceRatting());
        ratingAttitude.setRating(ratingDetail.getAttitudeRatting() == null ? 0 : ratingDetail.getAttitudeRatting());
    }

    public static void startActivity(BaseActivity baseActivity, int userId) {
        Intent intent = new Intent(baseActivity, RatingDetailActivity.class);
        intent.putExtra(USER_ID, userId);
        baseActivity.startActivity(intent);
    }
}
