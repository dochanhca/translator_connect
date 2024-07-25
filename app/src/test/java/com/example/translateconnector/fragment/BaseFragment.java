package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imoktranslator.activity.BaseActivity;
import com.imoktranslator.dialog.NotifyDialog;
import com.imoktranslator.presenter.BaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by TonTN on 05/04/2018
 */

public abstract class BaseFragment extends Fragment implements BaseView {

    protected String TAG;

    private Unbinder unbinder;

    protected abstract int getLayoutId();

    protected abstract void initViews();

    public abstract boolean isHandleBackPressedOnFragment();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void switchFragment(BaseFragment baseFragment, FragmentController.Option option) {
        ((FragmentBox) getActivity()).switchFragment(baseFragment, option);
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
        getBaseActivity().notify(errMessage);
    }

    public void showNotifyDialog(String message) {
        getBaseActivity().showNotifyDialog(message);
    }

    public void showNotifyDialog(String message, String positiveTitle) {
        getBaseActivity().showNotifyDialog(message, positiveTitle);
    }

    public void showNotifyDialog(String message, NotifyDialog.OnNotifyCallback callback) {
        getBaseActivity().showNotifyDialog(message, callback);
    }

    public void showNotifyDialog(String message, String positiveTitle, NotifyDialog.OnNotifyCallback callback) {
        getBaseActivity().showNotifyDialog(message, positiveTitle, callback);
    }
}
