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

public class ReasonCancelOrderDialog extends BaseDialog {

    @BindView(R.id.edt_reason)
    OpenSansEditText edtReason;
    @BindView(R.id.txt_ok)
    OpenSansBoldTextView txtOk;
    @BindView(R.id.txt_cancel)
    OpenSansBoldTextView txtCancel;

    private OnReasonCancelDialogCallback onReasonCancelDialogCallback;

    public static void showDialog(FragmentManager fragmentManager, OnReasonCancelDialogCallback onReasonCancelDialogCallback) {
        ReasonCancelOrderDialog reasonCancelOrderDialog = new ReasonCancelOrderDialog();

        reasonCancelOrderDialog.onReasonCancelDialogCallback = onReasonCancelDialogCallback;
        reasonCancelOrderDialog.show(fragmentManager, ReasonCancelOrderDialog.class.getSimpleName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_reason_cancel_order;
    }

    @OnClick({R.id.txt_ok, R.id.txt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:
                if (onReasonCancelDialogCallback != null) {
                    onReasonCancelDialogCallback.onOk(edtReason.getText().toString());
                }
                dismiss();
                break;
            case R.id.txt_cancel:
                dismiss();
                break;
        }
    }

    public interface OnReasonCancelDialogCallback {
        void onOk(String reason);
    }
}
