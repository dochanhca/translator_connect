package com.example.translateconnector.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.imoktranslator.R;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/25/18.
 */

public class ProgressDialog extends Dialog {

    @BindView(R.id.progress_avi)
    AVLoadingIndicatorView mLoadingIndicatorView;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_progress, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void showProgress() {
        this.show();
        this.mLoadingIndicatorView.smoothToShow();
    }

    public void hideProgress() {
        this.mLoadingIndicatorView.smoothToHide();
        this.dismiss();
    }
}
