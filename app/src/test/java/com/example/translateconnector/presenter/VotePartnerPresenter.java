package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.VotePartnerParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.DialogUtils;

public class VotePartnerPresenter extends BasePresenter {
    private VotePartnerView view;
    private Context context;

    public VotePartnerPresenter(Context context, VotePartnerView view) {
        super(context);
        this.context = context;
        this.view = view;
    }

    public void votePartner() {
        int orderId = view.getOrderId();
        int userId = view.getUserId();
        int partnerId = view.getPartnerId();
        float totalRatting = view.getTotalRatting();
        float skillRatting = view.getSkillRatting();
        float majorRatting = view.getMajorRatting();
        float priceRatting = view.getPriceRatting();
        float attitudeRatting = view.getAttitudeRatting();
        String content = view.getContent();

        VotePartnerParam votePartnerParam = new VotePartnerParam(orderId, userId, partnerId,
                totalRatting, skillRatting, majorRatting, priceRatting, attitudeRatting, content);
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().votePartner(votePartnerParam), new BaseRequest<Void>() {
            @Override
            public void onSuccess(Void response) {
                DialogUtils.hideProgress();
                view.voteSuccessful();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });

    }

    public void getPartnerInfo() {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().fetchUserInfo(view.getPartnerId()), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                view.getPartnerInfoSuccessful(response.getPersonalInfo());
                DialogUtils.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface VotePartnerView extends BaseView {

        int getUserId();

        float getTotalRatting();

        String getContent();

        float getAttitudeRatting();

        float getSkillRatting();

        float getMajorRatting();

        float getPriceRatting();

        void voteSuccessful();

        void getPartnerInfoSuccessful(PersonalInfo personalInfo);

        int getOrderId();

        int getPartnerId();
    }
}
