package com.example.translateconnector.presenter;

import android.content.Context;

import com.imoktranslator.model.RatingDetail;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.RatingDetailResponse;
import com.imoktranslator.utils.DialogUtils;

public class RatingDetailPresenter extends BasePresenter {
    private RatingDetailView view;
    private Context context;

    public RatingDetailPresenter(Context context, RatingDetailView view) {
        super(context);
        this.view = view;
        this.context = context;
    }

    public void getRatingDetail(int userId) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().getRatingDetail(userId), new BaseRequest<RatingDetailResponse>() {
            @Override
            public void onSuccess(RatingDetailResponse response) {
                DialogUtils.hideProgress();
                view.getRatingDetailSuccessful(response.getRatingDetail());
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public interface RatingDetailView extends BaseView {

        void getRatingDetailSuccessful(RatingDetail ratingDetail);
    }
}
