package com.example.translateconnector.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansTextView;

import butterknife.BindView;

/**
 * Created by ducpv on 3/24/18.
 */

public class NotifyDialog extends BaseDialog {

    @BindView(R.id.txt_message)
    OpenSansTextView txtMessage;
    @BindView(R.id.divider_line)
    View dividerLine;
    @BindView(R.id.txt_ok)
    TextView txtOk;
    @BindView(R.id.txt_cancel)
    TextView txtCancel;

    private String message;
    private boolean hideNegativeButton;
    private OnNotifyCallback listener;
    private String positiveTitle;
    private String negativeTitle;

    public void showDialog(FragmentManager fragmentManager,
                           String message, String positiveTitle, String negativeTitle,
                           boolean hideNegativeButton, OnNotifyCallback listener) {
        NotifyDialog notifyDialog = new NotifyDialog();

        notifyDialog.message = message;
        notifyDialog.hideNegativeButton = hideNegativeButton;
        notifyDialog.positiveTitle = positiveTitle;
        notifyDialog.negativeTitle = negativeTitle;
        notifyDialog.listener = listener;

        try {
            notifyDialog.show(fragmentManager, NotifyDialog.class.getSimpleName());
        } catch (IllegalStateException e) {
            Log.e(NotifyDialog.class.getSimpleName(), "IllegalStateException: " + e.getMessage());
        }

    }

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_notify;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        initViews();
        initActions();
    }

    private void initActions() {
        txtOk.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOk();
            }
            dismiss();
        });

        txtCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });
    }

    private void initViews() {
        if (message != null) {
            txtMessage.setText(message);
        }

        if (positiveTitle != null) {
            txtOk.setText(positiveTitle);
        }

        if (negativeTitle != null) {
            txtCancel.setText(negativeTitle);
        }

        txtCancel.setVisibility(hideNegativeButton ? View.GONE : View.VISIBLE);
    }

    public interface OnNotifyCallback {
        void onCancel();

        void onOk(Object... obj);
    }

}
