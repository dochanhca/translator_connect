package com.example.translateconnector.presenter;

/**
 * Created by ton on 4/3/18.
 */

public interface BaseView {
    void showProgress();
    void hideProgress();
    void notify(String errMessage);
}
