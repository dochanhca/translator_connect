package com.example.translateconnector.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansEditText;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateStatusDialog extends BaseDialog {

    @BindView(R.id.edt_user_status)
    OpenSansEditText edtUserStatus;
    @BindView(R.id.txt_ok)
    OpenSansBoldTextView txtOk;
    @BindView(R.id.txt_cancel)
    OpenSansBoldTextView txtCancel;

    private OnUpdateStatusDialogCallBack onUpdateStatusDialogCallBack;

    public static void showDialog(FragmentManager fragmentManager,
                                  OnUpdateStatusDialogCallBack updateStatusDialogCallBack) {
        UpdateStatusDialog updateStatusDialog = new UpdateStatusDialog();

        updateStatusDialog.onUpdateStatusDialogCallBack = updateStatusDialogCallBack;
        updateStatusDialog.show(fragmentManager, ReasonCancelOrderDialog.class.getSimpleName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_update_status;
    }

    @OnClick({R.id.txt_ok, R.id.txt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:
                if (onUpdateStatusDialogCallBack != null) {
                    onUpdateStatusDialogCallBack.onOk(edtUserStatus.getText().toString());
                }
                dismiss();
                break;
            case R.id.txt_cancel:
                dismiss();
                break;
        }
    }

    public interface OnUpdateStatusDialogCallBack {
        void onOk(String status);
    }
}
