package com.example.translateconnector.fragment;

/**
 * Created by tvoer on 4/5/18.
 */

public interface FragmentBox {
    int getContainerViewId();

    void switchFragment(BaseFragment baseFragment, FragmentController.Option option);
}
