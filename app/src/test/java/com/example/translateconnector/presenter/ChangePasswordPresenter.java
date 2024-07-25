package com.example.translateconnector.presenter;

import android.content.Context;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.network.param.ChangePasswordParam;
import com.imoktranslator.network.param.UpdatePasswordParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.utils.DialogUtils;

/**
 * Created by ton on 3/31/18.
 */

public class ChangePasswordPresenter extends BasePresenter {
    private ChangePasswordView view;

    public ChangePasswordPresenter(Context context, ChangePasswordView view) {
        super(context);
        this.view = view;
    }

    public void confirmChangePassword(boolean isForgotPwd) {
        if (isForgotPwd) {
            if (view.getUserID() > 0) {
                DialogUtils.showProgress(getContext());
                requestAPI(getAPI().updatePassword(new UpdatePasswordParam(view.getUserID(), view.getNewPassword())), new BaseRequest<UpdatePasswordParam>() {

                    @Override
                    public void onSuccess(UpdatePasswordParam response) {
                        Toast.makeText(getContext(), getContext().getString(R.string.TB_2005), Toast.LENGTH_SHORT).show();
                        DialogUtils.hideProgress();
                        view.backToLogin();
                    }

                    @Override
                    public void onFailure(int errCode, String errMessage) {
                        DialogUtils.hideProgress();
                        showNetworkError(view, errCode, errMessage);
                    }
                });
            }
        } else {
            DialogUtils.showProgress(getContext());
            requestAPI(getAPI().changePassword(new ChangePasswordParam(view.getOldPassword(), view.getNewPassword())), new BaseRequest<ChangePasswordParam>() {

                @Override
                public void onSuccess(ChangePasswordParam response) {
                    Toast.makeText(getContext(), getContext().getString(R.string.TB_2005), Toast.LENGTH_SHORT).show();
                    DialogUtils.hideProgress();
                    view.onBackPress();
                }

                @Override
                public void onFailure(int errCode, String errMessage) {
                    DialogUtils.hideProgress();
                    showNetworkError(view, errCode, errMessage);
                }
            });

        }

    }

    public interface ChangePasswordView extends BaseView {

        int getUserID();

        String getNewPassword();

        String getOldPassword();

        void backToLogin();

        void onBackPress();
    }
}
