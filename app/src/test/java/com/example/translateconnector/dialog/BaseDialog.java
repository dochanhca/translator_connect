package com.example.translateconnector.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.presenter.BaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ducpv on 3/24/18.
 */

public abstract class BaseDialog extends DialogFragment implements BaseView {

    protected abstract int getDialogLayout();

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getDialogLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void showProgress() {
        getBaseActivity().showProgress();
    }

    @Override
    public void hideProgress() {
        getBaseActivity().hideProgress();
    }

    @Override
    public void notify(String errMessage) {
        Toast.makeText(getBaseActivity(),errMessage, Toast.LENGTH_LONG).show();
    }

    protected void setupDialog(int width, int height) {
        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        //set dialog size
        getDialog().getWindow().setLayout(width,
                height);
    }
}
